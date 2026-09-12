package madoku.craft.java.core.time;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

/** Keeps the client-side day/night clock in step with the server clock rate. */
public final class MadokuClientTimeManager {
	private static volatile float serverClockRate = 1.0F;
	private static volatile double worldTimeRemainder = 0.0D;
	private static volatile boolean hasObservedWorldTime = false;
	private static volatile long lastObservedWorldTime = 0L;
	private static boolean initialized;

	private MadokuClientTimeManager() { }

	public static void initialize() {
		if (initialized) {
			return;
		}
		initialized = true;
		ClientPlayNetworking.registerGlobalReceiver(TimePayloadAPIManager.TYPE,
			(payload, context) -> context.client().execute(() -> setServerClockRate(payload.rate())));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> reset());
	}

	public static void setServerClockRate(float rate) {
		serverClockRate = Float.isFinite(rate) && rate > 0.0F ? rate : 1.0F;
		worldTimeRemainder = 0.0D;
		hasObservedWorldTime = false;
	}

	public static void reset() {
		serverClockRate = 1.0F;
		worldTimeRemainder = 0.0D;
		hasObservedWorldTime = false;
		lastObservedWorldTime = 0L;
	}

	public static long resolveNextWorldTime(ClientLevel level, long vanillaNextDayTime) {
		if (level == null || level.dimension() != Level.OVERWORLD) {
			return vanillaNextDayTime;
		}

		long currentDayTime = level.getDayTime();
		if (hasObservedWorldTime && currentDayTime < lastObservedWorldTime) {
			worldTimeRemainder = 0.0D;
		}
		hasObservedWorldTime = true;
		lastObservedWorldTime = currentDayTime;

		float rate = serverClockRate;
		if (!Float.isFinite(rate) || rate <= 0.0F || rate == 1.0F) {
			return vanillaNextDayTime;
		}

		worldTimeRemainder += rate;
		long wholeTicks = (long) Math.floor(worldTimeRemainder);
		worldTimeRemainder -= wholeTicks;
		long resolvedTime = wholeTicks <= 0L ? currentDayTime : safeAdd(currentDayTime, wholeTicks);
		lastObservedWorldTime = resolvedTime;
		return resolvedTime;
	}

	private static long safeAdd(long base, long delta) {
		try {
			return Math.addExact(base, delta);
		} catch (ArithmeticException exception) {
			return delta >= 0L ? Long.MAX_VALUE : Long.MIN_VALUE;
		}
	}
}

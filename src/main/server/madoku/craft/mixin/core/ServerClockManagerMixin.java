package madoku.craft.mixin.core;

import madoku.craft.java.core.time.TimeAPIManager;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerLevel.class)
public abstract class ServerClockManagerMixin {
	@ModifyArg(
		method = "tickTime",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"
		),
		index = 0
	)
	private long madokuCraft$applyConfiguredWorldTimeRate(long vanillaNextDayTime) {
		return TimeAPIManager.resolveNextWorldTime((ServerLevel) (Object) this, vanillaNextDayTime);
	}
}

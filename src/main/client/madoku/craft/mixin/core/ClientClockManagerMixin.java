package madoku.craft.mixin.core;

import madoku.craft.java.core.time.MadokuClientTimeManager;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientLevel.class)
public abstract class ClientClockManagerMixin {
	@ModifyArg(
		method = "tickTime",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;setDayTime(J)V"
		),
		index = 0
	)
	private long madokuCraft$applyServerWorldTimeRate(long vanillaNextDayTime) {
		return MadokuClientTimeManager.resolveNextWorldTime((ClientLevel) (Object) this, vanillaNextDayTime);
	}
}

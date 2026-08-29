package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.ParticleAdmissionBudget;
import dev.fpstune.config.FPSTuneConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
	@Unique
	private int fpstune$acceptedThisTick;

	@Inject(method = "tick", at = @At("HEAD"))
	private void fpstune$resetBudget(CallbackInfo callbackInfo) {
		fpstune$acceptedThisTick = 0;
	}

	@Inject(method = "add", at = @At("HEAD"), cancellable = true)
	private void fpstune$limitAdmission(Particle particle, CallbackInfo callbackInfo) {
		FPSTuneConfig config = FPSTuneClient.config();
		if (!ParticleAdmissionBudget.allows(fpstune$acceptedThisTick, config)) {
			callbackInfo.cancel();
			return;
		}
	}

	@Inject(
			method = "add",
			at = @At(value = "INVOKE", target = "Ljava/util/Queue;add(Ljava/lang/Object;)Z")
	)
	private void fpstune$countAdmission(Particle particle, CallbackInfo callbackInfo) {
		FPSTuneConfig config = FPSTuneClient.config();
		// This runs only at vanilla's queue.add calls, after ParticleLimit checks.
		fpstune$acceptedThisTick = ParticleAdmissionBudget.recordAccepted(fpstune$acceptedThisTick, config);
	}
}

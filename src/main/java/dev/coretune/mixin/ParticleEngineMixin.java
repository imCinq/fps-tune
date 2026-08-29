package dev.coretune.mixin;

import dev.coretune.CoreTuneClient;
import dev.coretune.ParticleAdmissionBudget;
import dev.coretune.config.CoreTuneConfig;
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
	private int coretune$acceptedThisTick;

	@Inject(method = "tick", at = @At("HEAD"))
	private void coretune$resetBudget(CallbackInfo callbackInfo) {
		coretune$acceptedThisTick = 0;
	}

	@Inject(method = "add", at = @At("HEAD"), cancellable = true)
	private void coretune$limitAdmission(Particle particle, CallbackInfo callbackInfo) {
		CoreTuneConfig config = CoreTuneClient.config();
		if (!ParticleAdmissionBudget.allows(coretune$acceptedThisTick, config)) {
			callbackInfo.cancel();
			return;
		}
	}

	@Inject(
			method = "add",
			at = @At(value = "INVOKE", target = "Ljava/util/Queue;add(Ljava/lang/Object;)Z")
	)
	private void coretune$countAdmission(Particle particle, CallbackInfo callbackInfo) {
		CoreTuneConfig config = CoreTuneClient.config();
		// This runs only at vanilla's queue.add calls, after ParticleLimit checks.
		coretune$acceptedThisTick = ParticleAdmissionBudget.recordAccepted(coretune$acceptedThisTick, config);
	}
}

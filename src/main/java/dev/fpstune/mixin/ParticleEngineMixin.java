package dev.fpstune.mixin;

import dev.fpstune.AdaptiveParticleBudgetController;
import dev.fpstune.FPSTuneClient;
import dev.fpstune.FPSTuneRenderPolicy;
import dev.fpstune.ParticleAdmissionBudget;
import dev.fpstune.ParticleAdmissionMetrics;
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
	@Unique
	private int fpstune$priorityAcceptedThisTick;
	@Unique
	private boolean fpstune$priorityForCurrentAdmission;

	@Inject(method = "tick", at = @At("HEAD"))
	private void fpstune$resetBudget(CallbackInfo callbackInfo) {
		fpstune$acceptedThisTick = 0;
		fpstune$priorityAcceptedThisTick = 0;
		fpstune$priorityForCurrentAdmission = false;
		ParticleAdmissionMetrics.beginTick();
	}

	@Inject(method = "add", at = @At("HEAD"), cancellable = true)
	private void fpstune$limitAdmission(Particle particle, CallbackInfo callbackInfo) {
		FPSTuneConfig config = FPSTuneClient.config();
		if (!FPSTuneRenderPolicy.shouldLimitParticles(config)) {
			return;
		}

		boolean priority = FPSTuneClient.isNearbyParticle(particle);
		fpstune$priorityForCurrentAdmission = priority;
		int totalBudget = AdaptiveParticleBudgetController.effectiveBudget(config);
		if (!ParticleAdmissionBudget.allows(
				fpstune$acceptedThisTick,
				fpstune$priorityAcceptedThisTick,
				priority,
				config,
				totalBudget
		)) {
			ParticleAdmissionMetrics.recordRejected(priority);
			callbackInfo.cancel();
		}
	}

	@Inject(
			method = "add",
			at = @At(value = "INVOKE", target = "Ljava/util/Queue;add(Ljava/lang/Object;)Z")
	)
	private void fpstune$countAdmission(Particle particle, CallbackInfo callbackInfo) {
		FPSTuneConfig config = FPSTuneClient.config();
		if (!FPSTuneRenderPolicy.shouldLimitParticles(config)) {
			return;
		}

		boolean priority = fpstune$priorityForCurrentAdmission;
		// This runs only at vanilla's queue.add calls, after admission checks.
		fpstune$acceptedThisTick = ParticleAdmissionBudget.recordAccepted(
				fpstune$acceptedThisTick,
				config,
				AdaptiveParticleBudgetController.effectiveBudget(config)
		);
		fpstune$priorityAcceptedThisTick = ParticleAdmissionBudget.recordPriorityAccepted(
				fpstune$priorityAcceptedThisTick,
				priority,
				config
		);
		ParticleAdmissionMetrics.recordAccepted(priority);
	}
}

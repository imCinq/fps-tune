package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.ParticleAdmissionBudget;
import dev.fpstune.ParticleAdmissionMetrics;
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
	@Unique
	private ParticleAdmissionBudget.RuntimeSnapshot fpstune$runtimeSnapshot;

	@Inject(method = "tick", at = @At("HEAD"))
	private void fpstune$resetBudget(CallbackInfo callbackInfo) {
		fpstune$acceptedThisTick = 0;
		fpstune$priorityAcceptedThisTick = 0;
		fpstune$priorityForCurrentAdmission = false;
		fpstune$runtimeSnapshot = ParticleAdmissionBudget.snapshot(FPSTuneClient.config());
		ParticleAdmissionMetrics.beginTick(
				fpstune$runtimeSnapshot.pressureTrackingEnabled(),
				fpstune$runtimeSnapshot.totalBudget()
		);
	}

	@Inject(method = "add", at = @At("HEAD"), cancellable = true)
	private void fpstune$limitAdmission(Particle particle, CallbackInfo callbackInfo) {
		ParticleAdmissionBudget.RuntimeSnapshot snapshot = fpstune$getRuntimeSnapshot();
		if (!snapshot.limitsParticles()) {
			return;
		}
		if (snapshot.pressureTrackingEnabled()) {
			ParticleAdmissionMetrics.recordPressureAttempt();
		}

		if (fpstune$acceptedThisTick >= snapshot.totalBudget()) {
			if (snapshot.pressureTrackingEnabled()) {
				ParticleAdmissionMetrics.recordPressureRejectionAtTotalBudget();
			}
			if (snapshot.detailedMetricsEnabled()) {
				boolean priority = snapshot.prioritizeNearbyParticles()
						&& FPSTuneClient.isNearbyParticle(particle, snapshot.nearbyRadiusSquared());
				ParticleAdmissionMetrics.recordRejected(priority);
			}
			callbackInfo.cancel();
			return;
		}

		boolean priority = snapshot.prioritizeNearbyParticles()
				&& FPSTuneClient.isNearbyParticle(particle, snapshot.nearbyRadiusSquared());
		fpstune$priorityForCurrentAdmission = priority;
		if (!ParticleAdmissionBudget.allows(
				fpstune$acceptedThisTick,
				fpstune$priorityAcceptedThisTick,
				priority,
				snapshot
		)) {
			if (snapshot.detailedMetricsEnabled()) {
				ParticleAdmissionMetrics.recordRejected(priority);
			}
			callbackInfo.cancel();
		}
	}

	@Inject(
			method = "add",
			at = @At(value = "INVOKE", target = "Ljava/util/Queue;add(Ljava/lang/Object;)Z")
	)
	private void fpstune$countAdmission(Particle particle, CallbackInfo callbackInfo) {
		ParticleAdmissionBudget.RuntimeSnapshot snapshot = fpstune$getRuntimeSnapshot();
		if (!snapshot.limitsParticles()) {
			return;
		}

		boolean priority = fpstune$priorityForCurrentAdmission;
		// This runs only at vanilla's queue.add calls, after admission checks.
		fpstune$acceptedThisTick = ParticleAdmissionBudget.recordAccepted(
				fpstune$acceptedThisTick,
				snapshot
		);
		fpstune$priorityAcceptedThisTick = ParticleAdmissionBudget.recordPriorityAccepted(
				fpstune$priorityAcceptedThisTick,
				priority,
				snapshot
		);
		if (snapshot.detailedMetricsEnabled()) {
			ParticleAdmissionMetrics.recordAccepted(priority);
		}
	}

	@Unique
	private ParticleAdmissionBudget.RuntimeSnapshot fpstune$getRuntimeSnapshot() {
		if (fpstune$runtimeSnapshot == null) {
			fpstune$runtimeSnapshot = ParticleAdmissionBudget.snapshot(FPSTuneClient.config());
		}
		return fpstune$runtimeSnapshot;
	}
}

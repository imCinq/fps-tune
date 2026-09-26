package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.ParticleAdmissionBudget;
import dev.fpstune.ParticleAdmissionMetrics;
import dev.fpstune.ParticleCounts;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
	// Render type to particle group (1.21.11+) or queue (1.21.1); ParticleCounts reads either.
	@Shadow
	@Final
	private Map<?, ?> particles;
	@Unique
	private int fpstune$acceptedThisTick;
	@Unique
	private int fpstune$priorityAcceptedThisTick;
	@Unique
	private boolean fpstune$priorityForCurrentAdmission;
	@Unique
	private ParticleAdmissionBudget.RuntimeSnapshot fpstune$runtimeSnapshot;
	@Unique
	private int fpstune$liveAtTickStart;
	@Unique
	private int fpstune$admittedLastTick;
	@Unique
	private int fpstune$admittedThisTick;

	@Inject(method = "tick", at = @At("HEAD"))
	private void fpstune$resetBudget(CallbackInfo callbackInfo) {
		fpstune$acceptedThisTick = 0;
		fpstune$priorityAcceptedThisTick = 0;
		fpstune$priorityForCurrentAdmission = false;
		fpstune$runtimeSnapshot = ParticleAdmissionBudget.snapshot(FPSTuneClient.config());
		// Particles admitted before this tick are still queued, so they stay in the live estimate.
		fpstune$admittedLastTick = fpstune$admittedThisTick;
		fpstune$admittedThisTick = 0;
		fpstune$liveAtTickStart = fpstune$runtimeSnapshot.activeParticleCap() > 0 ? fpstune$countLiveParticles() : 0;
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
		// Far-away and over-cap particles are skipped before the per-tick budget,
		// so they neither use it up nor count as Adaptive pressure.
		if ((snapshot.limitsDistance()
				&& !FPSTuneClient.isNearbyParticle(particle, snapshot.maxDistanceSquared()))
				|| ParticleAdmissionBudget.reachesActiveCap(
						fpstune$liveAtTickStart + fpstune$admittedLastTick + fpstune$admittedThisTick,
						snapshot
				)) {
			if (snapshot.detailedMetricsEnabled()) {
				ParticleAdmissionMetrics.recordRejected(false);
			}
			callbackInfo.cancel();
			return;
		}
		// Records every admission attempt, including particles vanilla's own
		// ParticleLimit then rejects: this is the Adaptive pressure signal, not the
		// diagnostics accepted/rejected counters (which exclude vanilla rejections).
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
		fpstune$admittedThisTick++;
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
	private int fpstune$countLiveParticles() {
		int live = 0;
		for (Object group : particles.values()) {
			live += ParticleCounts.size(group);
		}
		return live;
	}

	@Unique
	private ParticleAdmissionBudget.RuntimeSnapshot fpstune$getRuntimeSnapshot() {
		if (fpstune$runtimeSnapshot == null) {
			fpstune$runtimeSnapshot = ParticleAdmissionBudget.snapshot(FPSTuneClient.config());
		}
		return fpstune$runtimeSnapshot;
	}
}

package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

/**
 * Small, allocation-free budget logic shared by the particle mixin and tests.
 */
public final class ParticleAdmissionBudget {
	private ParticleAdmissionBudget() {
	}

	public static boolean allows(int accepted, FPSTuneConfig config) {
		if (!FPSTuneRenderPolicy.shouldLimitParticles(config)) {
			return true;
		}
		int budget = Math.max(0, config.maxParticlesPerTick);
		return accepted < budget;
	}

	public static int recordAccepted(int accepted, FPSTuneConfig config) {
		if (!FPSTuneRenderPolicy.shouldLimitParticles(config)) {
			return accepted;
		}
		int budget = Math.max(0, config.maxParticlesPerTick);
		return accepted >= budget ? accepted : accepted + 1;
	}
}

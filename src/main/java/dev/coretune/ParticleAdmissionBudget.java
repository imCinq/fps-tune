package dev.coretune;

import dev.coretune.config.CoreTuneConfig;

/**
 * Small, allocation-free budget logic shared by the particle mixin and tests.
 */
public final class ParticleAdmissionBudget {
	private ParticleAdmissionBudget() {
	}

	public static boolean allows(int accepted, CoreTuneConfig config) {
		if (config == null || !config.enabled) {
			return true;
		}
		int budget = Math.max(0, config.maxParticlesPerTick);
		return accepted < budget;
	}

	public static int recordAccepted(int accepted, CoreTuneConfig config) {
		if (config == null || !config.enabled) {
			return accepted;
		}
		int budget = Math.max(0, config.maxParticlesPerTick);
		return accepted >= budget ? accepted : accepted + 1;
	}
}

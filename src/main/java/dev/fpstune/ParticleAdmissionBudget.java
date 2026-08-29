package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

/**
 * Small, allocation-free budget logic shared by the particle mixin and tests.
 */
public final class ParticleAdmissionBudget {
	private ParticleAdmissionBudget() {
	}

	/**
	 * Preserves the original unclassified budget behavior for callers that do not
	 * have particle position information. The particle mixin uses the tiered
	 * overload below.
	 */
	public static boolean allows(int accepted, FPSTuneConfig config) {
		if (!FPSTuneRenderPolicy.shouldLimitParticles(config)) {
			return true;
		}
		return accepted < effectiveBudget(config);
	}

	public static int recordAccepted(int accepted, FPSTuneConfig config) {
		return recordAccepted(accepted, config, effectiveBudget(config));
	}

	public static int recordAccepted(int accepted, FPSTuneConfig config, int totalBudget) {
		if (!FPSTuneRenderPolicy.shouldLimitParticles(config)) {
			return accepted;
		}
		int budget = Math.max(0, totalBudget);
		return accepted >= budget ? accepted : accepted + 1;
	}

	/**
	 * Checks a particle against the total budget and a protected nearby-particle
	 * reserve. General particles cannot consume the reserve; nearby particles can
	 * use the reserve first and then any remaining general capacity.
	 */
	public static boolean allows(
			int accepted,
			int priorityAccepted,
			boolean priority,
			FPSTuneConfig config
	) {
		return allows(accepted, priorityAccepted, priority, config, effectiveBudget(config));
	}

	public static boolean allows(
			int accepted,
			int priorityAccepted,
			boolean priority,
			FPSTuneConfig config,
			int totalBudget
	) {
		if (!FPSTuneRenderPolicy.shouldLimitParticles(config)) {
			return true;
		}

		int budget = Math.max(0, totalBudget);
		if (accepted >= budget) {
			return false;
		}
		if (!config.prioritizeNearbyParticles) {
			return true;
		}

		int reserve = effectivePriorityReserve(config, budget);
		if (reserve == 0) {
			return true;
		}

		int generalBudget = budget - reserve;
		int generalAccepted = Math.max(0, accepted - priorityAccepted);
		return priority || generalAccepted < generalBudget;
	}

	public static int recordPriorityAccepted(int priorityAccepted, boolean priority, FPSTuneConfig config) {
		if (!FPSTuneRenderPolicy.shouldLimitParticles(config) || !priority) {
			return priorityAccepted;
		}
		return priorityAccepted + 1;
	}

	public static int effectiveBudget(FPSTuneConfig config) {
		return AdaptiveParticleBudgetController.effectiveBudget(config);
	}

	public static int effectivePriorityReserve(FPSTuneConfig config) {
		return effectivePriorityReserve(config, effectiveBudget(config));
	}

	public static int effectivePriorityReserve(FPSTuneConfig config, int totalBudget) {
		if (config == null || !config.prioritizeNearbyParticles) {
			return 0;
		}
		return Math.min(Math.max(0, totalBudget), Math.max(0, config.nearbyParticleReserve));
	}
}

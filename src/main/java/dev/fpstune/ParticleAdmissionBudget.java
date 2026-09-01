package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

/**
 * Small, allocation-free budget logic shared by the particle mixin and tests.
 */
public final class ParticleAdmissionBudget {
	private static final int PRIORITY_RESERVE_PERCENT = 50;

	private ParticleAdmissionBudget() {
	}

	/**
	 * Captures the normalized admission state used for one client particle tick.
	 * The snapshot is intentionally immutable and client-thread-only.
	 */
	public static RuntimeSnapshot snapshot(FPSTuneConfig config) {
		if (config == null) {
			return new RuntimeSnapshot(false, false, 0, 0, false, 0.0, false, false);
		}

		boolean masterEnabled = config.enabled;
		boolean particleAdmissionEnabled = config.particleAdmissionEnabled;
		boolean adaptiveEnabled = config.adaptiveParticleBudgetEnabled;
		if (!masterEnabled || !particleAdmissionEnabled) {
			return new RuntimeSnapshot(
					masterEnabled,
					particleAdmissionEnabled,
					0,
					0,
					false,
					0.0,
					adaptiveEnabled,
					false
			);
		}

		int totalBudget = Math.max(0, AdaptiveParticleBudgetController.effectiveBudget(config));
		boolean prioritizeNearbyParticles = config.prioritizeNearbyParticles;
		int nearbyDistance = Math.max(0, Math.min(config.nearbyParticleDistance, 64));
		double nearbyRadiusSquared = (double) nearbyDistance * nearbyDistance;
		return new RuntimeSnapshot(
				masterEnabled,
				particleAdmissionEnabled,
				totalBudget,
				effectivePriorityReserve(config, totalBudget),
				prioritizeNearbyParticles,
				nearbyRadiusSquared,
				adaptiveEnabled,
				config.diagnosticsHudEnabled
		);
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

	/**
	 * Applies a captured admission snapshot without re-reading configuration or
	 * adaptive-controller state.
	 */
	public static boolean allows(
			int accepted,
			int priorityAccepted,
			boolean priority,
			RuntimeSnapshot snapshot
	) {
		if (snapshot == null || !snapshot.limitsParticles()) {
			return true;
		}

		int budget = Math.max(0, snapshot.totalBudget());
		if (accepted >= budget) {
			return false;
		}
		if (!snapshot.prioritizeNearbyParticles()) {
			return true;
		}

		int reserve = Math.max(0, Math.min(snapshot.effectivePriorityReserve(), budget));
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

	public static int recordAccepted(int accepted, RuntimeSnapshot snapshot) {
		if (snapshot == null || !snapshot.limitsParticles()) {
			return accepted;
		}
		int budget = Math.max(0, snapshot.totalBudget());
		return accepted >= budget ? accepted : accepted + 1;
	}

	public static int recordPriorityAccepted(
			int priorityAccepted,
			boolean priority,
			RuntimeSnapshot snapshot
	) {
		if (snapshot == null || !snapshot.limitsParticles() || !priority) {
			return priorityAccepted;
		}
		return priorityAccepted + 1;
	}

	public static int effectiveBudget(FPSTuneConfig config) {
		return AdaptiveParticleBudgetController.effectiveBudget(config);
	}

	/**
	 * The reserve follows the current effective budget so it cannot consume all
	 * general-particle capacity at low Adaptive budgets.
	 */
	public static int effectivePriorityReserve(FPSTuneConfig config) {
		return effectivePriorityReserve(config, effectiveBudget(config));
	}

	public static int effectivePriorityReserve(FPSTuneConfig config, int totalBudget) {
		if (config == null || !config.prioritizeNearbyParticles) {
			return 0;
		}
		int budget = Math.max(0, totalBudget);
		int configuredReserve = Math.max(0, config.nearbyParticleReserve);
		int maximumReserve = budget * PRIORITY_RESERVE_PERCENT / 100;
		return Math.min(configuredReserve, maximumReserve);
	}

	public record RuntimeSnapshot(
			boolean masterEnabled,
			boolean particleAdmissionEnabled,
			int totalBudget,
			int effectivePriorityReserve,
			boolean prioritizeNearbyParticles,
			double nearbyRadiusSquared,
			boolean adaptiveEnabled,
			boolean detailedMetricsEnabled
	) {
		public boolean limitsParticles() {
			return masterEnabled && particleAdmissionEnabled;
		}
	}
}

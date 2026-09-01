package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

/**
 * Text and visibility rules for the optional local diagnostics HUD.
 */
public final class FPSTuneDiagnostics {
	private FPSTuneDiagnostics() {
	}

	public static boolean shouldRender(FPSTuneConfig config) {
		return config != null && config.diagnosticsHudEnabled;
	}

	public static String[] lines(
			FPSTuneConfig config,
			ParticleAdmissionMetrics.Snapshot metrics
	) {
		return lines(config, metrics, AdaptiveParticleBudgetController.snapshot(config));
	}

	public static String[] lines(
			FPSTuneConfig config,
			ParticleAdmissionMetrics.Snapshot metrics,
			AdaptiveParticleBudgetController.Snapshot adaptive
	) {
		if (config == null) {
			return new String[]{"FPS Tune: unavailable"};
		}

		ParticleAdmissionMetrics.Snapshot safeMetrics = metrics == null
				? new ParticleAdmissionMetrics.Snapshot(0, 0, 0, 0)
				: metrics;
		AdaptiveParticleBudgetController.Snapshot safeAdaptive = adaptive == null
				? AdaptiveParticleBudgetController.snapshot(config)
				: adaptive;
		boolean controlsEnabled = config.enabled;
		boolean particleLimiterEnabled = controlsEnabled && config.particleAdmissionEnabled;
		int displayedBudget = safeAdaptive.currentBudget();
		String particleState = particleLimiterEnabled
				? safeMetrics.acceptedThisTick() + "/" + displayedBudget + " admitted"
				: "inactive";
		String rejectedState = particleLimiterEnabled
				? safeMetrics.rejectedThisTick() + " this tick"
				: "inactive";
		String nearbyState = particleLimiterEnabled && config.prioritizeNearbyParticles
				? safeMetrics.priorityAcceptedThisTick() + " admitted / "
						+ ParticleAdmissionBudget.effectivePriorityReserve(config, displayedBudget) + " reserve"
				: "off";
		String targetState = config.adaptiveTargetAuto
				? "Auto->" + safeAdaptive.targetFps() + " FPS"
				: safeAdaptive.targetFps() + " FPS";
		String budgetState = !particleLimiterEnabled
				? "inactive"
				: config.adaptiveParticleBudgetEnabled
						? safeAdaptive.currentBudget() + " (" + safeAdaptive.minimumBudget() + "-"
								+ safeAdaptive.maximumBudget() + " @ " + targetState + ", "
								+ safeAdaptive.direction().label() + ")"
						: displayedBudget + " fixed";
		String weatherState = controlsEnabled && !config.weatherRenderingEnabled
				? "suppressed"
				: "vanilla";

		return new String[]{
				"FPS Tune: " + (controlsEnabled ? "ON" : "OFF"),
				"Particles: " + particleState,
				"Budget: " + budgetState,
				"Rejected: " + rejectedState,
				"Nearby: " + nearbyState,
				"Weather: " + weatherState
		};
	}
}

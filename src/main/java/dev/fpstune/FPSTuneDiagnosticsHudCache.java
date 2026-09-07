package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * Caches diagnostics text until a displayed value changes and caches its
 * measured width until either the text or font resources change.
 */
public final class FPSTuneDiagnosticsHudCache {
	private DisplayKey cachedKey;
	private String[] cachedLines = new String[0];
	private int cachedWidth;
	private boolean widthValid;

	public View update(
			FPSTuneConfig config,
			ParticleAdmissionMetrics.Snapshot metrics,
			AdaptiveParticleBudgetController.Snapshot adaptive,
			ToIntFunction<String> widthOf
	) {
		Objects.requireNonNull(widthOf, "widthOf");
		DisplayKey key = DisplayKey.from(config, metrics, adaptive);
		if (!key.equals(cachedKey)) {
			cachedKey = key;
			cachedLines = FPSTuneDiagnostics.lines(config, metrics, adaptive);
			widthValid = false;
		}
		if (!widthValid) {
			cachedWidth = 0;
			for (String line : cachedLines) {
				cachedWidth = Math.max(cachedWidth, widthOf.applyAsInt(line));
			}
			widthValid = true;
		}
		return new View(cachedLines, cachedWidth);
	}

	public void invalidateWidths() {
		widthValid = false;
	}

	public record View(String[] lines, int width) {
	}

	private record DisplayKey(
			boolean controlsEnabled,
			boolean particleLimiterEnabled,
			int accepted,
			int rejected,
			boolean nearbyEnabled,
			int nearbyAccepted,
			int nearbyReserve,
			boolean adaptiveEnabled,
			int budget,
			int minimumBudget,
			int maximumBudget,
			int targetFps,
			AdaptiveParticleBudgetController.Direction direction,
			boolean autoTarget,
			boolean weatherSuppressed
	) {
		private static DisplayKey from(
				FPSTuneConfig config,
				ParticleAdmissionMetrics.Snapshot metrics,
				AdaptiveParticleBudgetController.Snapshot adaptive
		) {
			if (config == null) {
				return new DisplayKey(
						false, false, 0, 0, false, 0, 0, false,
						0, 0, 0, 0, AdaptiveParticleBudgetController.Direction.HOLDING,
						false, false
				);
			}

			ParticleAdmissionMetrics.Snapshot safeMetrics = metrics == null
					? new ParticleAdmissionMetrics.Snapshot(0, 0, 0, 0)
					: metrics;
			AdaptiveParticleBudgetController.Snapshot safeAdaptive = adaptive == null
					? AdaptiveParticleBudgetController.snapshot(config)
					: adaptive;
			boolean controlsEnabled = config.enabled;
			boolean particleLimiterEnabled = controlsEnabled && config.particleAdmissionEnabled;
			boolean nearbyEnabled = particleLimiterEnabled && config.prioritizeNearbyParticles;
			boolean adaptiveEnabled = particleLimiterEnabled && config.adaptiveParticleBudgetEnabled;
			int budget = particleLimiterEnabled ? safeAdaptive.currentBudget() : 0;
			return new DisplayKey(
					controlsEnabled,
					particleLimiterEnabled,
					particleLimiterEnabled ? safeMetrics.acceptedThisTick() : 0,
					particleLimiterEnabled ? safeMetrics.rejectedThisTick() : 0,
					nearbyEnabled,
					nearbyEnabled ? safeMetrics.priorityAcceptedThisTick() : 0,
					nearbyEnabled ? ParticleAdmissionBudget.effectivePriorityReserve(config, budget) : 0,
					adaptiveEnabled,
					budget,
					adaptiveEnabled ? safeAdaptive.minimumBudget() : 0,
					adaptiveEnabled ? safeAdaptive.maximumBudget() : 0,
					adaptiveEnabled ? safeAdaptive.targetFps() : 0,
					adaptiveEnabled ? safeAdaptive.direction() : AdaptiveParticleBudgetController.Direction.FIXED,
					adaptiveEnabled && config.adaptiveTargetAuto,
					controlsEnabled && !config.weatherRenderingEnabled
			);
		}
	}
}

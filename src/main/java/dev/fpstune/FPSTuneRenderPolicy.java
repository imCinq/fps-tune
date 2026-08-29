package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

/**
 * Pure feature gates shared by render controllers and their tests.
 */
public final class FPSTuneRenderPolicy {
	private FPSTuneRenderPolicy() {
	}

	public static boolean shouldLimitParticles(FPSTuneConfig config) {
		return config != null && config.enabled && config.particleAdmissionEnabled;
	}

	public static boolean shouldRenderWeather(FPSTuneConfig config) {
		return config == null || !config.enabled || config.weatherRenderingEnabled;
	}
}

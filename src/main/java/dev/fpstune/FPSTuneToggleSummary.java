package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * The plain-language parts of the F6 message, kept free of Minecraft classes so
 * they can be unit tested.
 */
public final class FPSTuneToggleSummary {
	private FPSTuneToggleSummary() {
	}

	/** Translation keys describing what the player will see change, in display order. */
	public static List<String> keys(FPSTuneConfig config) {
		if (!config.enabled) {
			return List.of("message.fpstune.summary.vanilla");
		}

		List<String> keys = new ArrayList<>();
		if (config.particleAdmissionEnabled) {
			keys.add(config.adaptiveParticleBudgetEnabled
					? "message.fpstune.summary.particles_auto"
					: "message.fpstune.summary.particles");
		}
		if (config.weatherMode == FPSTuneConfig.WeatherMode.REDUCED) {
			keys.add("message.fpstune.summary.weather_reduced");
		} else if (config.weatherMode == FPSTuneConfig.WeatherMode.OFF) {
			keys.add("message.fpstune.summary.weather_off");
		}
		if (keys.isEmpty()) {
			keys.add("message.fpstune.summary.nothing");
		}
		return keys;
	}
}

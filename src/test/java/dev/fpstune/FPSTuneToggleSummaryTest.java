package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class FPSTuneToggleSummaryTest {
	@Test
	void offSaysTheGameRunsAsNormal() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.weatherMode = FPSTuneConfig.WeatherMode.OFF;

		assertEquals(List.of("message.fpstune.summary.vanilla"), FPSTuneToggleSummary.keys(config));
	}

	@Test
	void onListsWhatThePlayerWillSeeChange() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.adaptiveParticleBudgetEnabled = true;
		config.weatherMode = FPSTuneConfig.WeatherMode.OFF;

		assertEquals(
				List.of("message.fpstune.summary.particles_auto", "message.fpstune.summary.weather_off"),
				FPSTuneToggleSummary.keys(config)
		);
	}

	@Test
	void onWithEveryControlOffSaysSo() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = false;

		assertEquals(List.of("message.fpstune.summary.nothing"), FPSTuneToggleSummary.keys(config));
	}
}

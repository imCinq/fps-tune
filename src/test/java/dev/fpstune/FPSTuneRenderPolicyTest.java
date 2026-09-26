package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneRenderPolicyTest {
	@Test
	void missingConfigKeepsVanillaRenderPasses() {
		assertTrue(FPSTuneRenderPolicy.shouldRenderWeather(null));
		assertFalse(FPSTuneRenderPolicy.shouldLimitParticles(null));
	}

	@Test
	void disabledMasterSwitchIsAlwaysAVanillaPassThrough() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = false;
		config.weatherMode = FPSTuneConfig.WeatherMode.OFF;
		config.particleAdmissionEnabled = true;

		assertTrue(FPSTuneRenderPolicy.shouldRenderWeather(config));
		assertFalse(FPSTuneRenderPolicy.shouldLimitParticles(config));
	}

	@Test
	void weatherControllerCanBeDisabledIndependently() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.weatherMode = FPSTuneConfig.WeatherMode.OFF;

		assertFalse(FPSTuneRenderPolicy.shouldRenderWeather(config));
	}

	@Test
	void enabledModulesRemainVanillaByDefault() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = true;
		config.weatherMode = FPSTuneConfig.WeatherMode.VANILLA;

		assertTrue(FPSTuneRenderPolicy.shouldLimitParticles(config));
		assertTrue(FPSTuneRenderPolicy.shouldRenderWeather(config));
	}

	@Test
	void controllersRemainIndependentWhenOneIsDisabled() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = true;
		config.weatherMode = FPSTuneConfig.WeatherMode.OFF;

		assertTrue(FPSTuneRenderPolicy.shouldLimitParticles(config));
		assertFalse(FPSTuneRenderPolicy.shouldRenderWeather(config));

		config.particleAdmissionEnabled = false;
		config.weatherMode = FPSTuneConfig.WeatherMode.VANILLA;

		assertFalse(FPSTuneRenderPolicy.shouldLimitParticles(config));
		assertTrue(FPSTuneRenderPolicy.shouldRenderWeather(config));
	}
}

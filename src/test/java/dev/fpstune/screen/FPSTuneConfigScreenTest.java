package dev.fpstune.screen;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneConfigScreenTest {
	@Test
	void defaultSettingsUseTheBalancedProfile() {
		assertEquals(
				FPSTuneConfigScreen.PerformanceProfile.BALANCED,
				FPSTuneConfigScreen.profileFor(new FPSTuneConfig())
		);
	}

	@Test
	void selectingASmootherFramesProfileEnablesTheBoundedAutomaticLimit() {
		FPSTuneConfig config = new FPSTuneConfig();

		FPSTuneConfigScreen.applyProfile(
				config,
				FPSTuneConfigScreen.PerformanceProfile.SMOOTHER_FRAMES
		);

		assertTrue(config.particleAdmissionEnabled);
		assertEquals(150, config.maxParticlesPerTick);
		assertTrue(config.adaptiveParticleBudgetEnabled);
		assertEquals(120, config.adaptiveTargetFps);
		assertEquals(100, config.adaptiveMinParticlesPerTick);
		assertEquals(300, config.adaptiveMaxParticlesPerTick);
		assertEquals(
				FPSTuneConfigScreen.PerformanceProfile.SMOOTHER_FRAMES,
				FPSTuneConfigScreen.profileFor(config)
		);
	}

	@Test
	void changingAnAdvancedValueMakesTheProfileCustom() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.maxParticlesPerTick = 301;

		assertEquals(
				FPSTuneConfigScreen.PerformanceProfile.CUSTOM,
				FPSTuneConfigScreen.profileFor(config)
		);
	}

	@Test
	void choosingCustomLeavesTheExistingDraftUntouched() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.maxParticlesPerTick = 512;
		config.adaptiveParticleBudgetEnabled = true;

		FPSTuneConfigScreen.applyProfile(
				config,
				FPSTuneConfigScreen.PerformanceProfile.CUSTOM
		);

		assertEquals(512, config.maxParticlesPerTick);
		assertTrue(config.adaptiveParticleBudgetEnabled);
		assertFalse(config.enabled);
	}
}

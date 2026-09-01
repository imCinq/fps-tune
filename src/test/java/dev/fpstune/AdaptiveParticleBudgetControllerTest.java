package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AdaptiveParticleBudgetControllerTest {
	@BeforeEach
	void resetController() {
		AdaptiveParticleBudgetController.reset(null);
	}

	@Test
	void startsFromTheFixedBudgetAndMovesDownAfterASlowFrameStreak() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);

		AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressured());
		for (int index = 0; index < 15; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressured());
		}

		AdaptiveParticleBudgetController.Snapshot snapshot = AdaptiveParticleBudgetController.snapshot(config);
		assertEquals(270, snapshot.currentBudget());
		assertEquals(AdaptiveParticleBudgetController.Direction.DECREASING, snapshot.direction());
	}

	@Test
	void slowFramesWithoutParticlePressureHoldTheBudget() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);

		for (int index = 0; index < 100; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(20.0, config);
		}

		AdaptiveParticleBudgetController.Snapshot snapshot = AdaptiveParticleBudgetController.snapshot(config);
		assertEquals(300, snapshot.currentBudget());
		assertEquals(AdaptiveParticleBudgetController.Direction.HOLDING, snapshot.direction());
	}

	@Test
	void nearBudgetAttemptsCountAsPressureWithoutTotalRejection() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);
		ParticleAdmissionMetrics.PressureSnapshot pressure =
				new ParticleAdmissionMetrics.PressureSnapshot(225, 300, 0);

		AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressure);
		for (int index = 0; index < 15; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressure);
		}

		assertEquals(270, AdaptiveParticleBudgetController.snapshot(config).currentBudget());
	}

	@Test
	void severePressuredFramesTriggerAQuarterBudgetCut() {
		FPSTuneConfig config = adaptiveConfig();
		config.maxParticlesPerTick = 1_000;
		config.adaptiveMinParticlesPerTick = 100;
		config.adaptiveMaxParticlesPerTick = 2_000;
		config.adaptiveTargetFps = 100;
		config.clamp();
		AdaptiveParticleBudgetController.reset(config);

		for (int index = 0; index < 3; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(30.0, config, pressured());
		}

		AdaptiveParticleBudgetController.Snapshot snapshot =
				AdaptiveParticleBudgetController.snapshot(config);
		assertEquals(750, snapshot.currentBudget());
		assertEquals(AdaptiveParticleBudgetController.Direction.DECREASING, snapshot.direction());
	}

	@Test
	void severeFramesWithoutParticlePressureHoldTheBudget() {
		FPSTuneConfig config = adaptiveConfig();
		config.maxParticlesPerTick = 1_000;
		config.adaptiveTargetFps = 100;
		config.clamp();
		AdaptiveParticleBudgetController.reset(config);

		for (int index = 0; index < 20; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(30.0, config);
		}

		assertEquals(1_000, AdaptiveParticleBudgetController.snapshot(config).currentBudget());
	}

	@Test
	void changingTheEffectiveAutoTargetPreservesTheCurrentBudget() {
		FPSTuneConfig config = adaptiveConfig();
		config.adaptiveTargetAuto = true;
		AdaptiveParticleBudgetController.reset(config);

		for (int index = 0; index < 3; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(30.0, config, 100, pressured());
		}
		assertEquals(270, AdaptiveParticleBudgetController.snapshot(config).currentBudget());

		AdaptiveParticleBudgetController.observeFrameMillis(8.0, config, 60, pressured());

		AdaptiveParticleBudgetController.Snapshot snapshot =
				AdaptiveParticleBudgetController.snapshot(config);
		assertEquals(270, snapshot.currentBudget());
		assertEquals(60, snapshot.targetFps());
		assertEquals(8.0, snapshot.smoothedFrameTimeMillis());
	}

	@Test
	void movesUpOnlyAfterAHealthyStreak() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);

		AdaptiveParticleBudgetController.observeFrameMillis(5.0, config);
		for (int index = 0; index < 58; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(5.0, config);
		}
		assertEquals(300, AdaptiveParticleBudgetController.snapshot(config).currentBudget());

		AdaptiveParticleBudgetController.observeFrameMillis(5.0, config);

		AdaptiveParticleBudgetController.Snapshot snapshot = AdaptiveParticleBudgetController.snapshot(config);
		assertEquals(330, snapshot.currentBudget());
		assertEquals(AdaptiveParticleBudgetController.Direction.INCREASING, snapshot.direction());
	}

	@Test
	void neutralFrameTimesHoldTheBudgetWithoutOscillation() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);

		for (int index = 0; index < 200; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(10.0, config);
		}

		AdaptiveParticleBudgetController.Snapshot snapshot = AdaptiveParticleBudgetController.snapshot(config);
		assertEquals(300, snapshot.currentBudget());
		assertEquals(AdaptiveParticleBudgetController.Direction.HOLDING, snapshot.direction());
	}

	@Test
	void cooldownPreventsAnImmediateSecondAdjustment() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);

		AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressured());
		for (int index = 0; index < 15; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressured());
		}
		for (int index = 0; index < 30; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressured());
		}

		assertEquals(270, AdaptiveParticleBudgetController.snapshot(config).currentBudget());
	}

	@Test
	void ignoresInvalidLongIntervals() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);

		AdaptiveParticleBudgetController.observeFrame(1_000_000_000L, config);
		AdaptiveParticleBudgetController.observeFrame(1_300_000_000L, config);

		AdaptiveParticleBudgetController.Snapshot snapshot = AdaptiveParticleBudgetController.snapshot(config);
		assertEquals(300, snapshot.currentBudget());
		assertEquals(-1.0, snapshot.smoothedFrameTimeMillis());
	}

	@Test
	void ignoresNonFiniteFrameTimes() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);

		AdaptiveParticleBudgetController.observeFrameMillis(Double.NaN, config);
		AdaptiveParticleBudgetController.observeFrameMillis(Double.POSITIVE_INFINITY, config);

		assertEquals(-1.0, AdaptiveParticleBudgetController.snapshot(config).smoothedFrameTimeMillis());
	}

	@Test
	void disabledAdaptiveModeUsesTheFixedBudget() {
		FPSTuneConfig config = adaptiveConfig();
		config.adaptiveParticleBudgetEnabled = false;
		config.maxParticlesPerTick = 512;
		AdaptiveParticleBudgetController.reset(config);

		AdaptiveParticleBudgetController.observeFrameMillis(30.0, config);

		AdaptiveParticleBudgetController.Snapshot snapshot = AdaptiveParticleBudgetController.snapshot(config);
		assertEquals(512, AdaptiveParticleBudgetController.effectiveBudget(config));
		assertEquals(512, snapshot.currentBudget());
		assertEquals(AdaptiveParticleBudgetController.Direction.FIXED, snapshot.direction());
		assertFalse(config.adaptiveParticleBudgetEnabled);
	}

	@Test
	void currentBudgetStaysWithinConfiguredBounds() {
		FPSTuneConfig config = adaptiveConfig();
		config.maxParticlesPerTick = 50;
		config.adaptiveMinParticlesPerTick = 100;
		config.adaptiveMaxParticlesPerTick = 100;
		config.clamp();
		AdaptiveParticleBudgetController.reset(config);

		assertTrue(AdaptiveParticleBudgetController.effectiveBudget(config) >= 100);
		assertEquals(100, AdaptiveParticleBudgetController.snapshot(config).currentBudget());
	}

	@Test
	void changingAdaptiveSettingsStartsFromTheNewConfiguredBudget() {
		FPSTuneConfig config = adaptiveConfig();
		AdaptiveParticleBudgetController.reset(config);

		AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressured());
		for (int index = 0; index < 14; index++) {
			AdaptiveParticleBudgetController.observeFrameMillis(20.0, config, pressured());
		}
		assertEquals(270, AdaptiveParticleBudgetController.effectiveBudget(config));

		config.maxParticlesPerTick = 600;

		assertEquals(600, AdaptiveParticleBudgetController.effectiveBudget(config));
		assertEquals(-1.0, AdaptiveParticleBudgetController.snapshot(config).smoothedFrameTimeMillis());
	}

	private static ParticleAdmissionMetrics.PressureSnapshot pressured() {
		return new ParticleAdmissionMetrics.PressureSnapshot(300, 300, 1);
	}

	private static FPSTuneConfig adaptiveConfig() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = true;
		config.adaptiveParticleBudgetEnabled = true;
		config.maxParticlesPerTick = 300;
		config.adaptiveTargetFps = 100;
		config.adaptiveMinParticlesPerTick = 100;
		config.adaptiveMaxParticlesPerTick = 2_000;
		return config;
	}
}

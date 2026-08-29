package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneDiagnosticsTest {
	@Test
	void diagnosticsDescribeTheActiveLocalControllers() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = true;
		config.maxParticlesPerTick = 4;
		config.prioritizeNearbyParticles = true;
		config.nearbyParticleReserve = 2;
		config.weatherRenderingEnabled = false;
		config.diagnosticsHudEnabled = true;

		assertTrue(FPSTuneDiagnostics.shouldRender(config));
		assertArrayEquals(
				new String[]{
						"FPS Tune: ON",
						"Particles: 3/4 admitted",
						"Budget: 4 fixed",
						"Rejected: 6 this tick",
						"Nearby: 2 admitted / 2 reserve",
						"Weather: suppressed"
				},
				FPSTuneDiagnostics.lines(config, new ParticleAdmissionMetrics.Snapshot(3, 6, 2, 1))
		);
	}

	@Test
	void diagnosticsRemainExplicitWhenControlsAreDisabled() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.diagnosticsHudEnabled = true;
		config.weatherRenderingEnabled = false;

		assertTrue(FPSTuneDiagnostics.shouldRender(config));
		assertArrayEquals(
				new String[]{
						"FPS Tune: OFF",
						"Particles: inactive",
						"Budget: inactive",
						"Rejected: inactive",
						"Nearby: off",
						"Weather: vanilla"
				},
				FPSTuneDiagnostics.lines(config, new ParticleAdmissionMetrics.Snapshot(8, 12, 4, 2))
		);

		config.diagnosticsHudEnabled = false;
		assertFalse(FPSTuneDiagnostics.shouldRender(config));
	}

	@Test
	void diagnosticsShowTheCurrentAdaptiveBudgetAndDirection() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = true;
		config.prioritizeNearbyParticles = false;
		config.adaptiveParticleBudgetEnabled = true;
		config.diagnosticsHudEnabled = true;

		assertArrayEquals(
				new String[]{
						"FPS Tune: ON",
						"Particles: 4/270 admitted",
						"Budget: 270 (100-2000 @ 120 FPS, down)",
						"Rejected: 1 this tick",
						"Nearby: off",
						"Weather: vanilla"
				},
				FPSTuneDiagnostics.lines(
						config,
						new ParticleAdmissionMetrics.Snapshot(4, 1, 0, 0),
						new AdaptiveParticleBudgetController.Snapshot(
								270,
								100,
								2_000,
								120,
								10.0,
								AdaptiveParticleBudgetController.Direction.DECREASING
						)
				)
		);
	}
}

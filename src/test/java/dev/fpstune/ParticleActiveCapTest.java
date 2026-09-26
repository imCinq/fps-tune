package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ParticleActiveCapTest {
	@Test
	void capAndDistanceAreOffByDefault() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		ParticleAdmissionBudget.RuntimeSnapshot snapshot = ParticleAdmissionBudget.snapshot(config);

		assertEquals(0, snapshot.activeParticleCap());
		assertFalse(snapshot.limitsDistance());
		assertFalse(ParticleAdmissionBudget.reachesActiveCap(1_000_000, snapshot));
	}

	@Test
	void capStopsAdmissionOnceTheLiveEstimateReachesIt() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.activeParticleCapEnabled = true;
		config.maxActiveParticles = 2_000;
		ParticleAdmissionBudget.RuntimeSnapshot snapshot = ParticleAdmissionBudget.snapshot(config);

		assertFalse(ParticleAdmissionBudget.reachesActiveCap(1_999, snapshot));
		assertTrue(ParticleAdmissionBudget.reachesActiveCap(2_000, snapshot));
	}

	@Test
	void capAndDistanceNeedTheParticleSwitchAndMasterSwitch() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = false;
		config.activeParticleCapEnabled = true;
		config.distantParticleLimitEnabled = true;
		ParticleAdmissionBudget.RuntimeSnapshot snapshot = ParticleAdmissionBudget.snapshot(config);
		assertFalse(ParticleAdmissionBudget.reachesActiveCap(1_000_000, snapshot));
		assertFalse(snapshot.limitsDistance());

		config.particleAdmissionEnabled = true;
		config.enabled = false;
		snapshot = ParticleAdmissionBudget.snapshot(config);
		assertFalse(ParticleAdmissionBudget.reachesActiveCap(1_000_000, snapshot));
		assertFalse(snapshot.limitsDistance());
	}

	@Test
	void distanceLimitUsesTheSquaredRadius() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.distantParticleLimitEnabled = true;
		config.particleMaxDistance = 32;
		ParticleAdmissionBudget.RuntimeSnapshot snapshot = ParticleAdmissionBudget.snapshot(config);

		assertTrue(snapshot.limitsDistance());
		assertEquals(1_024.0, snapshot.maxDistanceSquared());
	}
}

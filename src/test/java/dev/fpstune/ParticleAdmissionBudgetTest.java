package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ParticleAdmissionBudgetTest {
	@Test
	void enabledBudgetStopsExactlyAtConfiguredLimit() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 2;

		assertTrue(ParticleAdmissionBudget.allows(0, config));
		assertTrue(ParticleAdmissionBudget.allows(1, config));
		assertFalse(ParticleAdmissionBudget.allows(2, config));
	}

	@Test
	void onlyAcceptedParticlesConsumeBudget() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 2;

		int accepted = 0;
		assertTrue(ParticleAdmissionBudget.allows(accepted, config));
		accepted = ParticleAdmissionBudget.recordAccepted(accepted, config);
		assertEquals(1, accepted);
		assertTrue(ParticleAdmissionBudget.allows(accepted, config));
		accepted = ParticleAdmissionBudget.recordAccepted(accepted, config);
		assertEquals(2, accepted);
		assertFalse(ParticleAdmissionBudget.allows(accepted, config));
	}

	@Test
	void disabledOrMissingConfigIsApassThrough() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = false;
		config.maxParticlesPerTick = 0;

		assertTrue(ParticleAdmissionBudget.allows(10_000, config));
		assertEquals(10_000, ParticleAdmissionBudget.recordAccepted(10_000, config));
		assertTrue(ParticleAdmissionBudget.allows(10_000, null));
	}

	@Test
	void particleControllerCanBeDisabledIndependently() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = false;
		config.maxParticlesPerTick = 0;

		assertTrue(ParticleAdmissionBudget.allows(10_000, config));
		assertEquals(10_000, ParticleAdmissionBudget.recordAccepted(10_000, config));
	}

	@Test
	void particleStormSimulationNeverExceedsBudget() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 300;

		int accepted = 0;
		int dropped = 0;
		for (int i = 0; i < 100_000; i++) {
			if (ParticleAdmissionBudget.allows(accepted, config)) {
				accepted = ParticleAdmissionBudget.recordAccepted(accepted, config);
			} else {
				dropped++;
			}
		}

		assertEquals(300, accepted);
		assertEquals(99_700, dropped);
	}

	@Test
	void defensiveCounterDoesNotCrossZeroBudget() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 0;

		assertFalse(ParticleAdmissionBudget.allows(0, config));
		assertEquals(0, ParticleAdmissionBudget.recordAccepted(0, config));
	}

	@Test
	void configClampKeepsBudgetInsideSupportedRange() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.maxParticlesPerTick = -1;
		config.clamp();
		assertEquals(0, config.maxParticlesPerTick);

		config.maxParticlesPerTick = 10_001;
		config.clamp();
		assertEquals(10_000, config.maxParticlesPerTick);
	}
}

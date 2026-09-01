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
	void nearbyReserveProtectsPriorityCapacityFromGeneralParticles() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 4;
		config.prioritizeNearbyParticles = true;
		config.nearbyParticleReserve = 2;

		assertEquals(2, ParticleAdmissionBudget.effectivePriorityReserve(config));
		assertTrue(ParticleAdmissionBudget.allows(0, 0, false, config));
		assertTrue(ParticleAdmissionBudget.allows(1, 0, false, config));
		assertFalse(ParticleAdmissionBudget.allows(2, 0, false, config));
		assertTrue(ParticleAdmissionBudget.allows(2, 0, true, config));
		assertTrue(ParticleAdmissionBudget.allows(3, 1, true, config));
		assertFalse(ParticleAdmissionBudget.allows(4, 2, true, config));
	}

	@Test
	void priorityParticlesCanUseUnreservedCapacityAfterReserveIsUsed() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 4;
		config.prioritizeNearbyParticles = true;
		config.nearbyParticleReserve = 2;

		assertTrue(ParticleAdmissionBudget.allows(2, 2, true, config));
		assertTrue(ParticleAdmissionBudget.allows(3, 3, true, config));
		assertFalse(ParticleAdmissionBudget.allows(4, 4, true, config));
	}

	@Test
	void priorityReserveIsCappedAtHalfOfTheCurrentBudget() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 4;
		config.nearbyParticleReserve = 99;

		assertEquals(2, ParticleAdmissionBudget.effectivePriorityReserve(config));
		assertTrue(ParticleAdmissionBudget.allows(1, 0, false, config));
		assertFalse(ParticleAdmissionBudget.allows(2, 0, false, config));
		assertTrue(ParticleAdmissionBudget.allows(2, 0, true, config));
		assertFalse(ParticleAdmissionBudget.allows(4, 4, true, config));
	}

	@Test
	void nearbyReserveScalesDownAtLowEffectiveBudgets() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 100;
		config.nearbyParticleReserve = 100;

		assertEquals(50, ParticleAdmissionBudget.effectivePriorityReserve(config));
		assertTrue(ParticleAdmissionBudget.allows(49, 0, false, config));
		assertFalse(ParticleAdmissionBudget.allows(50, 0, false, config));
		assertTrue(ParticleAdmissionBudget.allows(50, 0, true, config));
	}

	@Test
	void dynamicBudgetIsUsedForAdmissionAndAccounting() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 300;

		assertTrue(ParticleAdmissionBudget.allows(299, 0, false, config, 600));
		assertFalse(ParticleAdmissionBudget.allows(600, 0, false, config, 600));
		assertEquals(301, ParticleAdmissionBudget.recordAccepted(300, config, 600));
	}

	@Test
	void nearbyReserveUsesTheCurrentAdaptiveBudget() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.prioritizeNearbyParticles = true;
		config.nearbyParticleReserve = 100;

		assertEquals(100, ParticleAdmissionBudget.effectivePriorityReserve(config, 200));
		assertTrue(ParticleAdmissionBudget.allows(99, 0, false, config, 200));
		assertFalse(ParticleAdmissionBudget.allows(100, 0, false, config, 200));
		assertTrue(ParticleAdmissionBudget.allows(100, 0, true, config, 200));
		assertFalse(ParticleAdmissionBudget.allows(200, 100, true, config, 200));
	}

	@Test
	void runtimeSnapshotCapturesOneTickOfAdmissionState() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = true;
		config.maxParticlesPerTick = 300;
		config.prioritizeNearbyParticles = true;
		config.nearbyParticleReserve = 100;
		config.nearbyParticleDistance = 16;
		config.diagnosticsHudEnabled = true;

		ParticleAdmissionBudget.RuntimeSnapshot snapshot = ParticleAdmissionBudget.snapshot(config);

		assertTrue(snapshot.limitsParticles());
		assertEquals(300, snapshot.totalBudget());
		assertEquals(100, snapshot.effectivePriorityReserve());
		assertTrue(snapshot.prioritizeNearbyParticles());
		assertEquals(256.0, snapshot.nearbyRadiusSquared());
		assertFalse(snapshot.adaptiveEnabled());
		assertTrue(snapshot.detailedMetricsEnabled());

		config.maxParticlesPerTick = 4;
		config.prioritizeNearbyParticles = false;
		config.diagnosticsHudEnabled = false;

		assertEquals(300, snapshot.totalBudget());
		assertTrue(snapshot.prioritizeNearbyParticles());
		assertTrue(snapshot.detailedMetricsEnabled());
		assertTrue(ParticleAdmissionBudget.allows(299, 0, true, snapshot));
		assertFalse(ParticleAdmissionBudget.allows(300, 0, true, snapshot));
	}

	@Test
	void disablingPriorityRestoresTheFullBudgetForGeneralParticles() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 4;
		config.prioritizeNearbyParticles = false;
		config.nearbyParticleReserve = 2;

		assertTrue(ParticleAdmissionBudget.allows(3, 0, false, config));
		assertFalse(ParticleAdmissionBudget.allows(4, 0, false, config));
		assertEquals(0, ParticleAdmissionBudget.effectivePriorityReserve(config));
	}

	@Test
	void priorityCounterTracksOnlyAcceptedNearbyParticles() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.maxParticlesPerTick = 4;

		assertEquals(1, ParticleAdmissionBudget.recordPriorityAccepted(0, true, config));
		assertEquals(1, ParticleAdmissionBudget.recordPriorityAccepted(1, false, config));
		config.particleAdmissionEnabled = false;
		assertEquals(1, ParticleAdmissionBudget.recordPriorityAccepted(1, true, config));
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

		config.nearbyParticleReserve = -1;
		config.nearbyParticleDistance = 65;
		config.clamp();
		assertEquals(0, config.nearbyParticleReserve);
		assertEquals(64, config.nearbyParticleDistance);
	}
}

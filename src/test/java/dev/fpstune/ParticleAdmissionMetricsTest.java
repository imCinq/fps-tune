package dev.fpstune;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ParticleAdmissionMetricsTest {
	@BeforeEach
	void resetMetrics() {
		ParticleAdmissionMetrics.reset();
	}

	@Test
	void recordsAcceptedAndRejectedParticlesByTier() {
		ParticleAdmissionMetrics.recordAccepted(true);
		ParticleAdmissionMetrics.recordAccepted(false);
		ParticleAdmissionMetrics.recordRejected(true);
		ParticleAdmissionMetrics.recordRejected(false);

		assertEquals(
				new ParticleAdmissionMetrics.Snapshot(2, 2, 1, 1),
				ParticleAdmissionMetrics.snapshot()
		);
	}

	@Test
	void pressureTrackingCapturesAttemptsAndTotalBudgetRejections() {
		ParticleAdmissionMetrics.beginTick(true, 300);
		ParticleAdmissionMetrics.recordPressureAttempt();
		ParticleAdmissionMetrics.recordPressureAttempt();
		ParticleAdmissionMetrics.recordPressureRejectionAtTotalBudget();

		assertEquals(
				new ParticleAdmissionMetrics.PressureSnapshot(2, 300, 1),
				ParticleAdmissionMetrics.pressureSnapshot()
		);

		ParticleAdmissionMetrics.beginTick(false, 300);
		ParticleAdmissionMetrics.recordPressureAttempt();
		assertEquals(
				new ParticleAdmissionMetrics.PressureSnapshot(0, 0, 0),
				ParticleAdmissionMetrics.pressureSnapshot()
		);
	}

	@Test
	void beginningATickClearsThePreviousSnapshot() {
		ParticleAdmissionMetrics.recordAccepted(true);
		ParticleAdmissionMetrics.recordRejected(true);

		ParticleAdmissionMetrics.beginTick();

		assertEquals(
				new ParticleAdmissionMetrics.Snapshot(0, 0, 0, 0),
				ParticleAdmissionMetrics.snapshot()
		);
	}
}

package dev.fpstune;

/**
 * Local, per-tick counters used by the optional diagnostics HUD and Adaptive
 * pressure tracking.
 *
 * <p>The counters are intentionally not persisted or exported. Particle
 * admission and HUD rendering both happen on the client thread.</p>
 */
public final class ParticleAdmissionMetrics {
	private static final PressureSnapshot NO_PRESSURE_SNAPSHOT = new PressureSnapshot(0, 0, 0);

	private static int acceptedThisTick;
	private static int rejectedThisTick;
	private static int priorityAcceptedThisTick;
	private static int priorityRejectedThisTick;
	private static boolean pressureTrackingEnabled;
	private static int pressureAttemptsThisTick;
	private static int pressureBudgetThisTick;
	private static int pressureRejectedAtTotalBudgetThisTick;

	private ParticleAdmissionMetrics() {
	}

	public static void beginTick() {
		beginTick(false, 0);
	}

	public static void beginTick(boolean trackPressure, int totalBudget) {
		acceptedThisTick = 0;
		rejectedThisTick = 0;
		priorityAcceptedThisTick = 0;
		priorityRejectedThisTick = 0;
		pressureTrackingEnabled = trackPressure;
		pressureAttemptsThisTick = 0;
		pressureBudgetThisTick = Math.max(0, totalBudget);
		pressureRejectedAtTotalBudgetThisTick = 0;
	}

	public static void recordAccepted(boolean priority) {
		acceptedThisTick++;
		if (priority) {
			priorityAcceptedThisTick++;
		}
	}

	public static void recordRejected(boolean priority) {
		rejectedThisTick++;
		if (priority) {
			priorityRejectedThisTick++;
		}
	}

	public static void recordPressureAttempt() {
		if (pressureTrackingEnabled) {
			pressureAttemptsThisTick++;
		}
	}

	public static void recordPressureRejectionAtTotalBudget() {
		if (pressureTrackingEnabled) {
			pressureRejectedAtTotalBudgetThisTick++;
		}
	}

	public static Snapshot snapshot() {
		return new Snapshot(
				acceptedThisTick,
				rejectedThisTick,
				priorityAcceptedThisTick,
				priorityRejectedThisTick
		);
	}

	public static PressureSnapshot pressureSnapshot() {
		if (!pressureTrackingEnabled) {
			return NO_PRESSURE_SNAPSHOT;
		}
		return new PressureSnapshot(
				pressureAttemptsThisTick,
				pressureBudgetThisTick,
				pressureRejectedAtTotalBudgetThisTick
		);
	}

	static void reset() {
		beginTick();
	}

	public record Snapshot(
			int acceptedThisTick,
			int rejectedThisTick,
			int priorityAcceptedThisTick,
			int priorityRejectedThisTick
	) {
	}

	public record PressureSnapshot(
			int attemptedThisTick,
			int totalBudget,
			int rejectedAtTotalBudgetThisTick
	) {
	}
}

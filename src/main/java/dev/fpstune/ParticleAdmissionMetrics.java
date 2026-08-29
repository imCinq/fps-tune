package dev.fpstune;

/**
 * Local, per-tick counters used by the optional diagnostics HUD.
 *
 * <p>The counters are intentionally not persisted or exported. Particle
 * admission and HUD rendering both happen on the client thread.</p>
 */
public final class ParticleAdmissionMetrics {
	private static int acceptedThisTick;
	private static int rejectedThisTick;
	private static int priorityAcceptedThisTick;
	private static int priorityRejectedThisTick;

	private ParticleAdmissionMetrics() {
	}

	public static void beginTick() {
		acceptedThisTick = 0;
		rejectedThisTick = 0;
		priorityAcceptedThisTick = 0;
		priorityRejectedThisTick = 0;
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

	public static Snapshot snapshot() {
		return new Snapshot(
				acceptedThisTick,
				rejectedThisTick,
				priorityAcceptedThisTick,
				priorityRejectedThisTick
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
}

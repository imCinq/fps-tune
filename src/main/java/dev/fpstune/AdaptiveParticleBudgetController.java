package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

/**
 * A small client-thread controller that adjusts only the particle admission
 * budget from recent local frame times.
 */
public final class AdaptiveParticleBudgetController {
	private static final double EMA_ALPHA = 0.10;
	private static final double SLOW_FRAME_THRESHOLD = 1.10;
	private static final double HEALTHY_FRAME_THRESHOLD = 0.85;
	private static final int PARTICLE_PRESSURE_PERCENT = 75;
	private static final double EMERGENCY_FRAME_THRESHOLD = 2.00;
	private static final int EMERGENCY_FRAME_STREAK_LIMIT = 3;
	private static final int EMERGENCY_STEP_PERCENT = 25;
	private static final int SLOW_FRAME_STREAK_LIMIT = 15;
	private static final int HEALTHY_FRAME_STREAK_LIMIT = 60;
	private static final int COOLDOWN_FRAMES = 30;
	private static final long MAX_TRACKED_FRAME_TIME_NANOS = 250_000_000L;

	private static boolean initialized;
	private static boolean trackedEnabled;
	private static boolean trackedParticleAdmissionEnabled;
	private static boolean trackedAdaptiveEnabled;
	private static boolean trackedAdaptiveTargetAuto;
	private static int trackedFixedBudget;
	private static int trackedMinimumBudget;
	private static int trackedMaximumBudget;
	private static int trackedConfiguredTargetFps;
	private static int trackedTargetFps;
	private static int currentBudget;
	private static long lastFrameNanos;
	private static boolean hasLastFrame;
	private static double smoothedFrameTimeMillis = -1.0;
	private static int slowFrameStreak;
	private static int emergencyFrameStreak;
	private static int healthyFrameStreak;
	private static int cooldownFrames;
	private static Direction direction = Direction.HOLDING;

	private AdaptiveParticleBudgetController() {
	}

	public static void reset(FPSTuneConfig config) {
		if (config == null) {
			initialized = false;
			currentBudget = 0;
			lastFrameNanos = 0L;
			hasLastFrame = false;
			smoothedFrameTimeMillis = -1.0;
			slowFrameStreak = 0;
			emergencyFrameStreak = 0;
			healthyFrameStreak = 0;
			cooldownFrames = 0;
			direction = Direction.HOLDING;
			return;
		}

		trackedEnabled = config.enabled;
		trackedParticleAdmissionEnabled = config.particleAdmissionEnabled;
		trackedAdaptiveEnabled = config.adaptiveParticleBudgetEnabled;
		trackedAdaptiveTargetAuto = config.adaptiveTargetAuto;
		trackedFixedBudget = clampParticles(config.maxParticlesPerTick);
		trackedMinimumBudget = clampParticles(config.adaptiveMinParticlesPerTick);
		trackedMaximumBudget = clampParticles(config.adaptiveMaxParticlesPerTick);
		if (trackedMinimumBudget > trackedMaximumBudget) {
			trackedMaximumBudget = trackedMinimumBudget;
		}
		trackedConfiguredTargetFps = clampTargetFps(config.adaptiveTargetFps);
		trackedTargetFps = trackedConfiguredTargetFps;
		currentBudget = trackedAdaptiveEnabled
				? clamp(trackedFixedBudget, trackedMinimumBudget, trackedMaximumBudget)
				: trackedFixedBudget;
		lastFrameNanos = 0L;
		hasLastFrame = false;
		smoothedFrameTimeMillis = -1.0;
		slowFrameStreak = 0;
		emergencyFrameStreak = 0;
		healthyFrameStreak = 0;
		cooldownFrames = 0;
		direction = Direction.HOLDING;
		initialized = true;
	}

	/**
	 * Clears the frame clock and returns the adaptive budget to its configured
	 * starting point when no in-world render frames are available.
	 */
	public static void pause(FPSTuneConfig config) {
		reset(config);
	}

	public static int effectiveBudget(FPSTuneConfig config) {
		if (config == null) {
			return 0;
		}
		if (!config.adaptiveParticleBudgetEnabled) {
			return Math.max(0, config.maxParticlesPerTick);
		}
		ensureConfiguration(config);
		return currentBudget;
	}

	/**
	 * Records one in-world render interval. The caller supplies a monotonic clock
	 * value so this class remains deterministic and easy to test.
	 */
	public static void observeFrame(long nowNanos, FPSTuneConfig config) {
		observeFrame(nowNanos, config, configuredTargetFps(config), ParticleAdmissionMetrics.pressureSnapshot());
	}

	/**
	 * Records one in-world render interval and the pressure observed during the
	 * corresponding client particle tick.
	 */
	public static void observeFrame(
			long nowNanos,
			FPSTuneConfig config,
			ParticleAdmissionMetrics.PressureSnapshot pressure
	) {
		observeFrame(nowNanos, config, configuredTargetFps(config), pressure);
	}

	/**
	 * Records one in-world render interval using the target resolved by the
	 * target-specific Minecraft bridge.
	 */
	public static void observeFrame(
			long nowNanos,
			FPSTuneConfig config,
			int effectiveTargetFps,
			ParticleAdmissionMetrics.PressureSnapshot pressure
	) {
		if (config == null || !config.enabled || !config.particleAdmissionEnabled
				|| !config.adaptiveParticleBudgetEnabled) {
			lastFrameNanos = 0L;
			hasLastFrame = false;
			return;
		}

		ensureConfiguration(config);
		if (setEffectiveTargetFps(effectiveTargetFps)) {
			lastFrameNanos = nowNanos;
			hasLastFrame = true;
			return;
		}
		if (!hasLastFrame) {
			lastFrameNanos = nowNanos;
			hasLastFrame = true;
			return;
		}

		long frameTimeNanos = nowNanos - lastFrameNanos;
		lastFrameNanos = nowNanos;
		if (frameTimeNanos <= 0L || frameTimeNanos > MAX_TRACKED_FRAME_TIME_NANOS) {
			clearStreaks();
			return;
		}
		observeFrameMillis(frameTimeNanos / 1_000_000.0, config, pressure);
	}

	/** Package-private deterministic hook used by unit tests. */
	static void observeFrameMillis(double frameTimeMillis, FPSTuneConfig config) {
		observeFrameMillis(
				frameTimeMillis,
				config,
				configuredTargetFps(config),
				new ParticleAdmissionMetrics.PressureSnapshot(0, 0, 0)
		);
	}

	/** Package-private deterministic hook used by unit tests. */
	static void observeFrameMillis(
			double frameTimeMillis,
			FPSTuneConfig config,
			ParticleAdmissionMetrics.PressureSnapshot pressure
	) {
		observeFrameMillis(frameTimeMillis, config, configuredTargetFps(config), pressure);
	}

	/** Package-private deterministic hook used by unit tests. */
	static void observeFrameMillis(
			double frameTimeMillis,
			FPSTuneConfig config,
			int effectiveTargetFps,
			ParticleAdmissionMetrics.PressureSnapshot pressure
	) {
		if (config == null || !config.enabled || !config.particleAdmissionEnabled
				|| !config.adaptiveParticleBudgetEnabled || !Double.isFinite(frameTimeMillis) || frameTimeMillis <= 0.0) {
			return;
		}

		ensureConfiguration(config);
		setEffectiveTargetFps(effectiveTargetFps);
		if (smoothedFrameTimeMillis < 0.0) {
			smoothedFrameTimeMillis = frameTimeMillis;
		} else {
			smoothedFrameTimeMillis = smoothedFrameTimeMillis * (1.0 - EMA_ALPHA)
					+ frameTimeMillis * EMA_ALPHA;
		}

		if (cooldownFrames > 0) {
			cooldownFrames--;
			clearStreaks();
			return;
		}

		double targetFrameTimeMillis = 1_000.0 / trackedTargetFps;
		if (smoothedFrameTimeMillis > targetFrameTimeMillis * SLOW_FRAME_THRESHOLD) {
			healthyFrameStreak = 0;
			if (hasParticlePressure(pressure)) {
				slowFrameStreak++;
				if (smoothedFrameTimeMillis > targetFrameTimeMillis * EMERGENCY_FRAME_THRESHOLD) {
					emergencyFrameStreak++;
					if (emergencyFrameStreak >= EMERGENCY_FRAME_STREAK_LIMIT) {
						adjustBudget(false, true);
						clearStreaks();
					}
				} else {
					emergencyFrameStreak = 0;
					if (slowFrameStreak >= SLOW_FRAME_STREAK_LIMIT) {
						adjustBudget(false, false);
						clearStreaks();
					}
				}
			} else {
				clearStreaks();
				direction = Direction.HOLDING;
			}
		} else if (smoothedFrameTimeMillis < targetFrameTimeMillis * HEALTHY_FRAME_THRESHOLD) {
			healthyFrameStreak++;
			slowFrameStreak = 0;
			emergencyFrameStreak = 0;
			if (healthyFrameStreak >= HEALTHY_FRAME_STREAK_LIMIT) {
				adjustBudget(true, false);
				clearStreaks();
			}
		} else {
			clearStreaks();
			direction = Direction.HOLDING;
		}
	}

	public static Snapshot snapshot(FPSTuneConfig config) {
		if (config == null) {
			return new Snapshot(0, 0, 0, 0, -1.0, Direction.HOLDING);
		}
		if (!config.adaptiveParticleBudgetEnabled) {
			int fixedBudget = Math.max(0, config.maxParticlesPerTick);
			return new Snapshot(
					fixedBudget,
					fixedBudget,
					fixedBudget,
					clampTargetFps(config.adaptiveTargetFps),
					-1.0,
					Direction.FIXED
			);
		}

		ensureConfiguration(config);
		return new Snapshot(
				currentBudget,
				trackedMinimumBudget,
				trackedMaximumBudget,
				trackedTargetFps,
				smoothedFrameTimeMillis,
				direction
		);
	}

	private static boolean hasParticlePressure(ParticleAdmissionMetrics.PressureSnapshot pressure) {
		if (pressure == null || pressure.attemptedThisTick() <= 0) {
			return false;
		}
		if (pressure.rejectedAtTotalBudgetThisTick() > 0) {
			return true;
		}

		int totalBudget = pressure.totalBudget();
		if (totalBudget <= 0) {
			return false;
		}
		int threshold = Math.max(1, (totalBudget * PARTICLE_PRESSURE_PERCENT + 99) / 100);
		return pressure.attemptedThisTick() >= threshold;
	}

	private static void adjustBudget(boolean increase, boolean emergency) {
		int step = increase
				? Math.max(10, currentBudget / 10)
				: Math.max(10, emergency ? (currentBudget * EMERGENCY_STEP_PERCENT) / 100 : currentBudget / 10);
		int nextBudget = increase
				? Math.min(trackedMaximumBudget, currentBudget + step)
				: Math.max(trackedMinimumBudget, currentBudget - step);
		if (nextBudget == currentBudget) {
			direction = Direction.HOLDING;
			return;
		}

		currentBudget = nextBudget;
		direction = increase ? Direction.INCREASING : Direction.DECREASING;
		cooldownFrames = COOLDOWN_FRAMES;
	}

	private static void ensureConfiguration(FPSTuneConfig config) {
		if (!initialized
				|| trackedEnabled != config.enabled
				|| trackedParticleAdmissionEnabled != config.particleAdmissionEnabled
				|| trackedAdaptiveEnabled != config.adaptiveParticleBudgetEnabled
				|| trackedFixedBudget != clampParticles(config.maxParticlesPerTick)
				|| trackedMinimumBudget != clampParticles(config.adaptiveMinParticlesPerTick)
				|| trackedMaximumBudget != normalizedMaximum(config)
				|| trackedAdaptiveTargetAuto != config.adaptiveTargetAuto
				|| trackedConfiguredTargetFps != clampTargetFps(config.adaptiveTargetFps)) {
			reset(config);
		}
	}

	private static boolean setEffectiveTargetFps(int effectiveTargetFps) {
		int normalizedTarget = clampTargetFps(effectiveTargetFps);
		if (trackedTargetFps == normalizedTarget) {
			return false;
		}
		trackedTargetFps = normalizedTarget;
		lastFrameNanos = 0L;
		hasLastFrame = false;
		smoothedFrameTimeMillis = -1.0;
		clearStreaks();
		direction = Direction.HOLDING;
		return true;
	}

	private static int configuredTargetFps(FPSTuneConfig config) {
		return config == null ? 120 : clampTargetFps(config.adaptiveTargetFps);
	}

	private static int normalizedMaximum(FPSTuneConfig config) {
		int minimum = clampParticles(config.adaptiveMinParticlesPerTick);
		return Math.max(minimum, clampParticles(config.adaptiveMaxParticlesPerTick));
	}

	private static void clearStreaks() {
		slowFrameStreak = 0;
		emergencyFrameStreak = 0;
		healthyFrameStreak = 0;
	}

	private static int clampParticles(int value) {
		return Math.max(0, Math.min(value, 10_000));
	}

	private static int clampTargetFps(int value) {
		return Math.max(30, Math.min(value, 360));
	}

	private static int clamp(int value, int minimum, int maximum) {
		return Math.max(minimum, Math.min(value, maximum));
	}

	public enum Direction {
		INCREASING("up"),
		DECREASING("down"),
		HOLDING("steady"),
		FIXED("fixed");

		private final String label;

		Direction(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}
	}

	public record Snapshot(
			int currentBudget,
			int minimumBudget,
			int maximumBudget,
			int targetFps,
			double smoothedFrameTimeMillis,
			Direction direction
	) {
	}
}

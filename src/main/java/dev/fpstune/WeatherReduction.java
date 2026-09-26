package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;

/**
 * The Lighter rain and snow mode, shared by the per-version weather mixins:
 * precipitation is drawn in a smaller circle around the camera and every other
 * landing splash is skipped. Weather itself and its sounds are unchanged.
 */
public final class WeatherReduction {
	private WeatherReduction() {
	}

	public static boolean active(FPSTuneConfig config) {
		return config != null && config.enabled && config.weatherMode == FPSTuneConfig.WeatherMode.REDUCED;
	}

	/** Three quarters of vanilla's radius, rounded up, and never below three blocks. */
	public static int reducedRadius(int radius) {
		return Math.max(3, (radius * 3 + 3) / 4);
	}

	public static boolean keepsColumn(int columnX, int columnZ, double cameraX, double cameraZ, int radius) {
		double deltaX = columnX + 0.5 - cameraX;
		double deltaZ = columnZ + 0.5 - cameraZ;
		return deltaX * deltaX + deltaZ * deltaZ <= (double) radius * radius;
	}

	public static boolean keepsSplash(int splashIndex) {
		return (splashIndex & 1) == 0;
	}
}

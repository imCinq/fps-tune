package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class WeatherReductionTest {
	@Test
	void onlyTheLighterModeWithTheMasterSwitchReducesWeather() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.weatherMode = FPSTuneConfig.WeatherMode.REDUCED;
		assertFalse(WeatherReduction.active(config));

		config.enabled = true;
		assertTrue(WeatherReduction.active(config));

		config.weatherMode = FPSTuneConfig.WeatherMode.OFF;
		assertFalse(WeatherReduction.active(config));
		assertFalse(WeatherReduction.active(null));
	}

	@Test
	void radiusShrinksButNeverBelowThreeBlocks() {
		assertEquals(8, WeatherReduction.reducedRadius(10));
		assertEquals(4, WeatherReduction.reducedRadius(5));
		assertEquals(3, WeatherReduction.reducedRadius(1));
	}

	@Test
	void columnsOutsideTheCircleAreDropped() {
		assertTrue(WeatherReduction.keepsColumn(0, 0, 0.5, 0.5, 8));
		assertTrue(WeatherReduction.keepsColumn(7, 0, 0.5, 0.5, 8));
		assertFalse(WeatherReduction.keepsColumn(9, 0, 0.5, 0.5, 8));
		assertFalse(WeatherReduction.keepsColumn(6, 6, 0.5, 0.5, 8));
	}

	@Test
	void everyOtherSplashIsKept() {
		assertTrue(WeatherReduction.keepsSplash(0));
		assertFalse(WeatherReduction.keepsSplash(1));
		assertTrue(WeatherReduction.keepsSplash(2));
	}
}

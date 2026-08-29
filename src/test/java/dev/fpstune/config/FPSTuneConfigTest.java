package dev.fpstune.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneConfigTest {
	@Test
	void copyPreservesSettingsAndCanBeEditedIndependently() {
		FPSTuneConfig original = new FPSTuneConfig();
		original.enabled = true;
		original.particleAdmissionEnabled = false;
		original.maxParticlesPerTick = 512;
		original.weatherRenderingEnabled = false;

		FPSTuneConfig copy = original.copy();
		copy.enabled = false;
		copy.particleAdmissionEnabled = true;
		copy.maxParticlesPerTick = 100;
		copy.weatherRenderingEnabled = true;

		assertTrue(original.enabled);
		assertFalse(original.particleAdmissionEnabled);
		assertEquals(512, original.maxParticlesPerTick);
		assertFalse(original.weatherRenderingEnabled);
		assertFalse(copy.enabled);
		assertTrue(copy.particleAdmissionEnabled);
		assertEquals(100, copy.maxParticlesPerTick);
		assertTrue(copy.weatherRenderingEnabled);
	}
}

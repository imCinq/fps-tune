package dev.fpstune.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneConfigTest {
	@Test
	void defaultsKeepTheModOptInAndDiagnosticsOff() {
		FPSTuneConfig config = new FPSTuneConfig();

		assertFalse(config.enabled);
		assertTrue(config.particleAdmissionEnabled);
		assertEquals(300, config.maxParticlesPerTick);
		assertTrue(config.prioritizeNearbyParticles);
		assertEquals(100, config.nearbyParticleReserve);
		assertEquals(16, config.nearbyParticleDistance);
		assertFalse(config.diagnosticsHudEnabled);
		assertFalse(config.adaptiveParticleBudgetEnabled);
		assertTrue(config.adaptiveTargetAuto);
		assertEquals(120, config.adaptiveTargetFps);
		assertEquals(100, config.adaptiveMinParticlesPerTick);
		assertEquals(2_000, config.adaptiveMaxParticlesPerTick);
		assertTrue(config.weatherRenderingEnabled);
	}

	@Test
	void copyPreservesSettingsAndCanBeEditedIndependently() {
		FPSTuneConfig original = new FPSTuneConfig();
		original.enabled = true;
		original.particleAdmissionEnabled = false;
		original.maxParticlesPerTick = 512;
		original.prioritizeNearbyParticles = false;
		original.nearbyParticleReserve = 180;
		original.nearbyParticleDistance = 24;
		original.diagnosticsHudEnabled = true;
		original.adaptiveParticleBudgetEnabled = true;
		original.adaptiveTargetAuto = false;
		original.adaptiveTargetFps = 144;
		original.adaptiveMinParticlesPerTick = 80;
		original.adaptiveMaxParticlesPerTick = 1_800;
		original.weatherRenderingEnabled = false;

		FPSTuneConfig copy = original.copy();
		copy.enabled = false;
		copy.particleAdmissionEnabled = true;
		copy.maxParticlesPerTick = 100;
		copy.prioritizeNearbyParticles = true;
		copy.nearbyParticleReserve = 50;
		copy.nearbyParticleDistance = 8;
		copy.diagnosticsHudEnabled = false;
		copy.adaptiveParticleBudgetEnabled = false;
		copy.adaptiveTargetFps = 90;
		copy.adaptiveMinParticlesPerTick = 50;
		copy.adaptiveMaxParticlesPerTick = 600;
		copy.weatherRenderingEnabled = true;

		assertTrue(original.enabled);
		assertFalse(original.particleAdmissionEnabled);
		assertEquals(512, original.maxParticlesPerTick);
		assertFalse(original.prioritizeNearbyParticles);
		assertEquals(180, original.nearbyParticleReserve);
		assertEquals(24, original.nearbyParticleDistance);
		assertTrue(original.diagnosticsHudEnabled);
		assertTrue(original.adaptiveParticleBudgetEnabled);
		assertFalse(original.adaptiveTargetAuto);
		assertEquals(144, original.adaptiveTargetFps);
		assertEquals(80, original.adaptiveMinParticlesPerTick);
		assertEquals(1_800, original.adaptiveMaxParticlesPerTick);
		assertFalse(original.weatherRenderingEnabled);
		assertFalse(copy.enabled);
		assertTrue(copy.particleAdmissionEnabled);
		assertEquals(100, copy.maxParticlesPerTick);
		assertTrue(copy.prioritizeNearbyParticles);
		assertEquals(50, copy.nearbyParticleReserve);
		assertEquals(8, copy.nearbyParticleDistance);
		assertFalse(copy.diagnosticsHudEnabled);
		assertFalse(copy.adaptiveParticleBudgetEnabled);
		assertTrue(copy.adaptiveTargetAuto);
		assertEquals(90, copy.adaptiveTargetFps);
		assertEquals(50, copy.adaptiveMinParticlesPerTick);
		assertEquals(600, copy.adaptiveMaxParticlesPerTick);
		assertTrue(copy.weatherRenderingEnabled);
	}

	@Test
	void copyFromRestoresAndClampsTheDraftConfiguration() {
		FPSTuneConfig draft = new FPSTuneConfig();
		draft.enabled = true;
		draft.maxParticlesPerTick = 512;
		draft.weatherRenderingEnabled = false;

		FPSTuneConfig source = new FPSTuneConfig();
		source.enabled = true;
		source.maxParticlesPerTick = 20_000;
		source.adaptiveMinParticlesPerTick = 2_000;
		source.adaptiveMaxParticlesPerTick = 100;

		draft.copyFrom(source);

		assertTrue(draft.enabled);
		assertEquals(10_000, draft.maxParticlesPerTick);
		assertEquals(2_000, draft.adaptiveMinParticlesPerTick);
		assertEquals(2_000, draft.adaptiveMaxParticlesPerTick);
		assertTrue(draft.weatherRenderingEnabled);
	}

	@Test
	void resetAdvancedSettingsPreservesBasicSettings() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.diagnosticsHudEnabled = true;
		config.weatherRenderingEnabled = false;
		config.particleAdmissionEnabled = false;
		config.maxParticlesPerTick = 512;
		config.prioritizeNearbyParticles = false;
		config.nearbyParticleReserve = 180;
		config.nearbyParticleDistance = 24;
		config.adaptiveParticleBudgetEnabled = true;
		config.adaptiveTargetAuto = false;
		config.adaptiveTargetFps = 144;
		config.adaptiveMinParticlesPerTick = 80;
		config.adaptiveMaxParticlesPerTick = 1_800;

		config.resetAdvancedSettings();

		assertTrue(config.enabled);
		assertTrue(config.diagnosticsHudEnabled);
		assertFalse(config.weatherRenderingEnabled);
		assertTrue(config.particleAdmissionEnabled);
		assertEquals(300, config.maxParticlesPerTick);
		assertTrue(config.prioritizeNearbyParticles);
		assertEquals(100, config.nearbyParticleReserve);
		assertEquals(16, config.nearbyParticleDistance);
		assertFalse(config.adaptiveParticleBudgetEnabled);
		assertTrue(config.adaptiveTargetAuto);
		assertEquals(120, config.adaptiveTargetFps);
		assertEquals(100, config.adaptiveMinParticlesPerTick);
		assertEquals(2_000, config.adaptiveMaxParticlesPerTick);
	}

	@Test
	void clampKeepsAdaptiveSettingsInsideSafeBoundsAndOrdersBudget() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.adaptiveTargetFps = 10;
		config.adaptiveMinParticlesPerTick = 2_000;
		config.adaptiveMaxParticlesPerTick = 100;

		config.clamp();

		assertEquals(30, config.adaptiveTargetFps);
		assertEquals(2_000, config.adaptiveMinParticlesPerTick);
		assertEquals(2_000, config.adaptiveMaxParticlesPerTick);

		config.adaptiveTargetFps = 500;
		config.adaptiveMinParticlesPerTick = -1;
		config.adaptiveMaxParticlesPerTick = 10_001;
		config.clamp();

		assertEquals(360, config.adaptiveTargetFps);
		assertEquals(0, config.adaptiveMinParticlesPerTick);
		assertEquals(10_000, config.adaptiveMaxParticlesPerTick);
	}
}

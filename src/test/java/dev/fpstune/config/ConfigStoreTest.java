package dev.fpstune.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConfigStoreTest {
	@Test
	void invalidValuesFallBackAndOutOfRangeBudgetIsClamped(@TempDir Path runDirectory) throws Exception {
		Path configDirectory = runDirectory.resolve("config");
		Files.createDirectories(configDirectory);
		Files.writeString(configDirectory.resolve("fpstune.properties"),
				"configVersion=1\nenabled=maybe\nparticleAdmissionEnabled=maybe\nmaxParticlesPerTick=999999\n"
						+ "adaptiveTargetFps=10\nadaptiveMinParticlesPerTick=2000\nadaptiveMaxParticlesPerTick=100\n"
						+ "weatherRenderingEnabled=maybe\n");

		FPSTuneConfig config = ConfigStore.load(runDirectory);

		assertFalse(config.enabled);
		assertTrue(config.particleAdmissionEnabled);
		assertEquals(10_000, config.maxParticlesPerTick);
		assertEquals(30, config.adaptiveTargetFps);
		assertFalse(config.adaptiveTargetAuto);
		assertEquals(2_000, config.adaptiveMinParticlesPerTick);
		assertEquals(2_000, config.adaptiveMaxParticlesPerTick);
		assertTrue(config.weatherRenderingEnabled);
	}

	@Test
	void saveCanBeReloadedWithoutLeavingTemporaryFile(@TempDir Path runDirectory) throws Exception {
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

		ConfigStore.save(runDirectory, original);
		FPSTuneConfig reloaded = ConfigStore.load(runDirectory);

		assertTrue(reloaded.enabled);
		assertFalse(reloaded.particleAdmissionEnabled);
		assertEquals(512, reloaded.maxParticlesPerTick);
		assertFalse(reloaded.prioritizeNearbyParticles);
		assertEquals(180, reloaded.nearbyParticleReserve);
		assertEquals(24, reloaded.nearbyParticleDistance);
		assertTrue(reloaded.diagnosticsHudEnabled);
		assertTrue(reloaded.adaptiveParticleBudgetEnabled);
		assertFalse(reloaded.adaptiveTargetAuto);
		assertEquals(144, reloaded.adaptiveTargetFps);
		assertEquals(80, reloaded.adaptiveMinParticlesPerTick);
		assertEquals(1_800, reloaded.adaptiveMaxParticlesPerTick);
		assertFalse(reloaded.weatherRenderingEnabled);

		Properties persisted = new Properties();
		try (var input = Files.newInputStream(runDirectory.resolve("config").resolve("fpstune.properties"))) {
			persisted.load(input);
		}
		assertEquals("4", persisted.getProperty("configVersion"));
		assertEquals("false", persisted.getProperty("prioritizeNearbyParticles"));
		assertEquals("180", persisted.getProperty("nearbyParticleReserve"));
		assertEquals("24", persisted.getProperty("nearbyParticleDistance"));
		assertEquals("true", persisted.getProperty("diagnosticsHudEnabled"));
		assertEquals("true", persisted.getProperty("adaptiveParticleBudgetEnabled"));
		assertEquals("false", persisted.getProperty("adaptiveTargetAuto"));
		assertEquals("144", persisted.getProperty("adaptiveTargetFps"));
		assertEquals("80", persisted.getProperty("adaptiveMinParticlesPerTick"));
		assertEquals("1800", persisted.getProperty("adaptiveMaxParticlesPerTick"));
		assertFalse(Files.exists(runDirectory.resolve("config").resolve("fpstune.properties.tmp")));
	}

	@Test
	void legacyConfigurationKeepsNewControllersAtSafeDefaults(@TempDir Path runDirectory) throws Exception {
		Path configDirectory = runDirectory.resolve("config");
		Files.createDirectories(configDirectory);
		Files.writeString(configDirectory.resolve("fpstune.properties"),
				"enabled=true\nmaxParticlesPerTick=256\n");

		FPSTuneConfig config = ConfigStore.load(runDirectory);

		assertTrue(config.enabled);
		assertTrue(config.particleAdmissionEnabled);
		assertEquals(256, config.maxParticlesPerTick);
		assertTrue(config.prioritizeNearbyParticles);
		assertEquals(100, config.nearbyParticleReserve);
		assertEquals(16, config.nearbyParticleDistance);
		assertFalse(config.diagnosticsHudEnabled);
		assertFalse(config.adaptiveParticleBudgetEnabled);
		assertFalse(config.adaptiveTargetAuto);
		assertEquals(120, config.adaptiveTargetFps);
		assertEquals(100, config.adaptiveMinParticlesPerTick);
		assertEquals(2_000, config.adaptiveMaxParticlesPerTick);
		assertTrue(config.weatherRenderingEnabled);
	}

	@Test
	void oldProjectConfigIsLoadedAndCopiedToTheNewPath(@TempDir Path runDirectory) throws Exception {
		Path configDirectory = runDirectory.resolve("config");
		Files.createDirectories(configDirectory);
		Files.writeString(configDirectory.resolve("coretune.properties"),
				"configVersion=1\nenabled=true\nparticleAdmissionEnabled=false\nmaxParticlesPerTick=512\nweatherRenderingEnabled=false\n");

		FPSTuneConfig config = ConfigStore.load(runDirectory);

		assertTrue(config.enabled);
		assertFalse(config.particleAdmissionEnabled);
		assertEquals(512, config.maxParticlesPerTick);
		assertTrue(config.prioritizeNearbyParticles);
		assertEquals(100, config.nearbyParticleReserve);
		assertEquals(16, config.nearbyParticleDistance);
		assertFalse(config.diagnosticsHudEnabled);
		assertFalse(config.adaptiveParticleBudgetEnabled);
		assertEquals(120, config.adaptiveTargetFps);
		assertEquals(100, config.adaptiveMinParticlesPerTick);
		assertEquals(2_000, config.adaptiveMaxParticlesPerTick);
		assertFalse(config.weatherRenderingEnabled);
		assertTrue(Files.exists(configDirectory.resolve("coretune.properties")));
		assertTrue(Files.exists(configDirectory.resolve("fpstune.properties")));

		Properties migrated = new Properties();
		try (var input = Files.newInputStream(configDirectory.resolve("fpstune.properties"))) {
			migrated.load(input);
		}
		assertEquals("true", migrated.getProperty("enabled"));
		assertEquals("false", migrated.getProperty("particleAdmissionEnabled"));
		assertEquals("512", migrated.getProperty("maxParticlesPerTick"));
		assertEquals("true", migrated.getProperty("prioritizeNearbyParticles"));
		assertEquals("100", migrated.getProperty("nearbyParticleReserve"));
		assertEquals("16", migrated.getProperty("nearbyParticleDistance"));
		assertEquals("false", migrated.getProperty("diagnosticsHudEnabled"));
		assertEquals("false", migrated.getProperty("adaptiveParticleBudgetEnabled"));
		assertEquals("false", migrated.getProperty("adaptiveTargetAuto"));
		assertEquals("120", migrated.getProperty("adaptiveTargetFps"));
		assertEquals("100", migrated.getProperty("adaptiveMinParticlesPerTick"));
		assertEquals("2000", migrated.getProperty("adaptiveMaxParticlesPerTick"));
		assertEquals("false", migrated.getProperty("weatherRenderingEnabled"));
	}

	@Test
	void newConfigurationTakesPrecedenceOverLegacyFile(@TempDir Path runDirectory) throws Exception {
		Path configDirectory = runDirectory.resolve("config");
		Files.createDirectories(configDirectory);
		Files.writeString(configDirectory.resolve("coretune.properties"), "enabled=true\nmaxParticlesPerTick=100\n");
		Files.writeString(configDirectory.resolve("fpstune.properties"), "enabled=false\nmaxParticlesPerTick=900\n");

		FPSTuneConfig config = ConfigStore.load(runDirectory);

		assertFalse(config.enabled);
		assertEquals(900, config.maxParticlesPerTick);
	}

	@Test
	void futureConfigurationVersionDoesNotActivateUnknownControllerSettings(@TempDir Path runDirectory) throws Exception {
		Path configDirectory = runDirectory.resolve("config");
		Files.createDirectories(configDirectory);
		Files.writeString(configDirectory.resolve("fpstune.properties"),
				"configVersion=5\nenabled=true\nparticleAdmissionEnabled=false\nprioritizeNearbyParticles=false\nweatherRenderingEnabled=false\n");

		FPSTuneConfig config = ConfigStore.load(runDirectory);

		assertTrue(config.enabled);
		assertTrue(config.particleAdmissionEnabled);
		assertTrue(config.weatherRenderingEnabled);
	}
}

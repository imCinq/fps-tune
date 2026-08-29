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
				"configVersion=1\nenabled=maybe\nparticleAdmissionEnabled=maybe\nmaxParticlesPerTick=999999\nweatherRenderingEnabled=maybe\n");

		FPSTuneConfig config = ConfigStore.load(runDirectory);

		assertFalse(config.enabled);
		assertTrue(config.particleAdmissionEnabled);
		assertEquals(10_000, config.maxParticlesPerTick);
		assertTrue(config.weatherRenderingEnabled);
	}

	@Test
	void saveCanBeReloadedWithoutLeavingTemporaryFile(@TempDir Path runDirectory) throws Exception {
		FPSTuneConfig original = new FPSTuneConfig();
		original.enabled = true;
		original.particleAdmissionEnabled = false;
		original.maxParticlesPerTick = 512;
		original.weatherRenderingEnabled = false;

		ConfigStore.save(runDirectory, original);
		FPSTuneConfig reloaded = ConfigStore.load(runDirectory);

		assertTrue(reloaded.enabled);
		assertFalse(reloaded.particleAdmissionEnabled);
		assertEquals(512, reloaded.maxParticlesPerTick);
		assertFalse(reloaded.weatherRenderingEnabled);

		Properties persisted = new Properties();
		try (var input = Files.newInputStream(runDirectory.resolve("config").resolve("fpstune.properties"))) {
			persisted.load(input);
		}
		assertEquals("1", persisted.getProperty("configVersion"));
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
				"configVersion=2\nenabled=true\nparticleAdmissionEnabled=false\nweatherRenderingEnabled=false\n");

		FPSTuneConfig config = ConfigStore.load(runDirectory);

		assertTrue(config.enabled);
		assertTrue(config.particleAdmissionEnabled);
		assertTrue(config.weatherRenderingEnabled);
	}
}

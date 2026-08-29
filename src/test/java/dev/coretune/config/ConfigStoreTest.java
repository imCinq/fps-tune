package dev.coretune.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConfigStoreTest {
	@Test
	void invalidValuesFallBackAndOutOfRangeBudgetIsClamped(@TempDir Path runDirectory) throws Exception {
		Path configDirectory = runDirectory.resolve("config");
		Files.createDirectories(configDirectory);
		Files.writeString(configDirectory.resolve("coretune.properties"),
				"enabled=maybe\nmaxParticlesPerTick=999999\n");

		CoreTuneConfig config = ConfigStore.load(runDirectory);

		assertFalse(config.enabled);
		assertEquals(10_000, config.maxParticlesPerTick);
	}

	@Test
	void saveCanBeReloadedWithoutLeavingTemporaryFile(@TempDir Path runDirectory) {
		CoreTuneConfig original = new CoreTuneConfig();
		original.enabled = true;
		original.maxParticlesPerTick = 512;

		ConfigStore.save(runDirectory, original);
		CoreTuneConfig reloaded = ConfigStore.load(runDirectory);

		assertTrue(reloaded.enabled);
		assertEquals(512, reloaded.maxParticlesPerTick);
		assertFalse(Files.exists(runDirectory.resolve("config").resolve("coretune.properties.tmp")));
	}
}

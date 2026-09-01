package dev.fpstune.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public final class ConfigStore {
	private static final String FILE_NAME = "fpstune.properties";
	private static final String LEGACY_FILE_NAME = "coretune.properties";

	private ConfigStore() {
	}

	public static FPSTuneConfig load(Path runDirectory) {
		FPSTuneConfig config = new FPSTuneConfig();
		Path directory = runDirectory.resolve("config");
		Path path = directory.resolve(FILE_NAME);
		boolean migrated = false;
		boolean loaded = false;
		try {
			if (!Files.isRegularFile(path)) {
				Path legacyPath = directory.resolve(LEGACY_FILE_NAME);
				if (!Files.isRegularFile(legacyPath)) {
					return config;
				}
				path = legacyPath;
				migrated = true;
			}

			Properties properties = new Properties();
			try (InputStream input = Files.newInputStream(path)) {
				properties.load(input);
				config.enabled = getBoolean(properties, "enabled", config.enabled);
				config.maxParticlesPerTick = getInt(properties, "maxParticlesPerTick", config.maxParticlesPerTick);
				int configVersion = getInt(properties, "configVersion", 0);
				if (configVersion < 0 || configVersion > FPSTuneConfig.CURRENT_CONFIG_VERSION) {
					// Unknown versions must not activate newly introduced controller behavior.
					config.adaptiveTargetAuto = false;
				}
if (configVersion >= 0 && configVersion <= FPSTuneConfig.CURRENT_CONFIG_VERSION) {
					config.particleAdmissionEnabled = getBoolean(
							properties,
							"particleAdmissionEnabled",
							config.particleAdmissionEnabled
					);
					config.prioritizeNearbyParticles = getBoolean(
							properties,
							"prioritizeNearbyParticles",
							config.prioritizeNearbyParticles
					);
					config.nearbyParticleReserve = getInt(
							properties,
							"nearbyParticleReserve",
							config.nearbyParticleReserve
					);
					config.nearbyParticleDistance = getInt(
							properties,
							"nearbyParticleDistance",
							config.nearbyParticleDistance
					);
					config.diagnosticsHudEnabled = getBoolean(
							properties,
							"diagnosticsHudEnabled",
							config.diagnosticsHudEnabled
					);
					config.adaptiveParticleBudgetEnabled = getBoolean(
							properties,
							"adaptiveParticleBudgetEnabled",
							config.adaptiveParticleBudgetEnabled
					);
					if (configVersion >= 4) {
						config.adaptiveTargetAuto = getBoolean(
								properties,
								"adaptiveTargetAuto",
								config.adaptiveTargetAuto
						);
					} else {
						// v1.1/v1.2-pre-auto files retain their explicit numeric target.
						config.adaptiveTargetAuto = false;
					}
					config.adaptiveTargetFps = getInt(
							properties,
							"adaptiveTargetFps",
							config.adaptiveTargetFps
					);
					config.adaptiveMinParticlesPerTick = getInt(
							properties,
							"adaptiveMinParticlesPerTick",
							config.adaptiveMinParticlesPerTick
					);
					config.adaptiveMaxParticlesPerTick = getInt(
							properties,
							"adaptiveMaxParticlesPerTick",
							config.adaptiveMaxParticlesPerTick
					);
					config.weatherRenderingEnabled = getBoolean(
							properties,
							"weatherRenderingEnabled",
							config.weatherRenderingEnabled
					);
				}
			}
			loaded = true;
		} catch (IOException | IllegalArgumentException | SecurityException ignored) {
			// A broken config should never stop Minecraft from launching.
		}
		config.clamp();
		if (migrated && loaded) {
			save(runDirectory, config);
		}
		return config;
	}

	public static void save(Path runDirectory, FPSTuneConfig config) {
		config.clamp();
		Path directory = runDirectory.resolve("config");
		Path path = directory.resolve(FILE_NAME);
		Path temporaryPath = directory.resolve(FILE_NAME + ".tmp");
		Properties properties = new Properties();
		properties.setProperty("configVersion", Integer.toString(FPSTuneConfig.CURRENT_CONFIG_VERSION));
		properties.setProperty("enabled", Boolean.toString(config.enabled));
		properties.setProperty("particleAdmissionEnabled", Boolean.toString(config.particleAdmissionEnabled));
		properties.setProperty("maxParticlesPerTick", Integer.toString(config.maxParticlesPerTick));
		properties.setProperty("prioritizeNearbyParticles", Boolean.toString(config.prioritizeNearbyParticles));
		properties.setProperty("nearbyParticleReserve", Integer.toString(config.nearbyParticleReserve));
		properties.setProperty("nearbyParticleDistance", Integer.toString(config.nearbyParticleDistance));
		properties.setProperty("diagnosticsHudEnabled", Boolean.toString(config.diagnosticsHudEnabled));
		properties.setProperty("adaptiveParticleBudgetEnabled", Boolean.toString(config.adaptiveParticleBudgetEnabled));
		properties.setProperty("adaptiveTargetAuto", Boolean.toString(config.adaptiveTargetAuto));
		properties.setProperty("adaptiveTargetFps", Integer.toString(config.adaptiveTargetFps));
		properties.setProperty("adaptiveMinParticlesPerTick", Integer.toString(config.adaptiveMinParticlesPerTick));
		properties.setProperty("adaptiveMaxParticlesPerTick", Integer.toString(config.adaptiveMaxParticlesPerTick));
		properties.setProperty("weatherRenderingEnabled", Boolean.toString(config.weatherRenderingEnabled));

		try {
			Files.createDirectories(directory);
			try (OutputStream output = Files.newOutputStream(
					temporaryPath,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE
			)) {
				properties.store(output, "FPS Tune configuration");
			}
			try {
				Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (AtomicMoveNotSupportedException ignored) {
				Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException | SecurityException ignored) {
			// Configuration is optional; keep the current in-memory settings if disk writes fail.
		} finally {
			try {
				Files.deleteIfExists(temporaryPath);
			} catch (IOException | SecurityException ignored) {
				// Best-effort cleanup of an interrupted save.
			}
		}
	}

	private static boolean getBoolean(Properties properties, String key, boolean fallback) {
		String value = properties.getProperty(key);
		if (value == null) {
			return fallback;
		}
		if ("true".equalsIgnoreCase(value.trim())) {
			return true;
		}
		if ("false".equalsIgnoreCase(value.trim())) {
			return false;
		}
		return fallback;
	}

	private static int getInt(Properties properties, String key, int fallback) {
		try {
			return Integer.parseInt(properties.getProperty(key, Integer.toString(fallback)).trim());
		} catch (NumberFormatException ignored) {
			return fallback;
		}
	}
}

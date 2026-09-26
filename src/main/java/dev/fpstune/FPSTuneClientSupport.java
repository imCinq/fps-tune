package dev.fpstune;

import dev.fpstune.config.ConfigStore;
import dev.fpstune.config.FPSTuneConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;

import java.nio.file.Path;

/**
 * Loader-independent client logic behind every version's {@code FPSTuneClient}
 * entrypoint. The entrypoints keep the live configuration and delegate here.
 */
public final class FPSTuneClientSupport {
	private FPSTuneClientSupport() {
	}

	/**
	 * Resolves the Adaptive target from the configured client FPS limit when
	 * Auto is selected. The numeric setting remains the fallback when the client limit is
	 * unavailable.
	 */
	public static int effectiveAdaptiveTargetFps(FPSTuneConfig currentConfig) {
		if (currentConfig == null) {
			return 120;
		}
		int fallback = FPSTuneConfig.clampAdaptiveTargetFps(currentConfig.adaptiveTargetFps);
		if (!currentConfig.adaptiveTargetAuto) {
			return fallback;
		}

		Minecraft client = Minecraft.getInstance();
		int configuredLimit = client.options.framerateLimit().get();
		return configuredLimit > 0
				? FPSTuneConfig.clampAdaptiveTargetFps(configuredLimit)
				: fallback;
	}

	public static boolean isNearbyParticle(FPSTuneConfig config, Particle particle) {
		if (particle == null || config == null || !config.prioritizeNearbyParticles || config.nearbyParticleDistance <= 0) {
			return false;
		}
		double radius = config.nearbyParticleDistance;
		return isNearbyParticle(particle, radius * radius);
	}

	public static boolean isNearbyParticle(Particle particle, double radiusSquared) {
		if (particle == null || radiusSquared <= 0.0) {
			return false;
		}
		Minecraft client = Minecraft.getInstance();
		// Measure from what the player is looking through, which differs in spectator view.
		Entity viewer = client.getCameraEntity();
		if (viewer == null) {
			return false;
		}

		// Compute the existing bounding-box center directly to avoid allocating a Vec3.
		var bounds = particle.getBoundingBox();
		double centerX = (bounds.minX + bounds.maxX) * 0.5;
		double centerY = (bounds.minY + bounds.maxY) * 0.5;
		double centerZ = (bounds.minZ + bounds.maxZ) * 0.5;
		double deltaX = viewer.getX() - centerX;
		double deltaY = viewer.getY() - centerY;
		double deltaZ = viewer.getZ() - centerZ;
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ <= radiusSquared;
	}

	/** Clamps, activates, and saves a configuration, returning it as the new live one. */
	public static FPSTuneConfig commitConfig(Path runDirectory, FPSTuneConfig updatedConfig) {
		updatedConfig.clamp();
		AdaptiveParticleBudgetController.reset(updatedConfig);
		ConfigStore.save(runDirectory, updatedConfig);
		return updatedConfig;
	}
}

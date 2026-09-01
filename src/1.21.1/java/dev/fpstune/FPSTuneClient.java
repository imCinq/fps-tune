package dev.fpstune;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fpstune.config.ConfigStore;
import dev.fpstune.config.FPSTuneConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class FPSTuneClient implements ClientModInitializer {
	public static final String MOD_ID = "fpstune";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static FPSTuneConfig config;
	private static KeyMapping toggleKey;

	@Override
	public void onInitializeClient() {
		Minecraft client = Minecraft.getInstance();
		config = ConfigStore.load(client.gameDirectory.toPath());
		AdaptiveParticleBudgetController.reset(config);
		FPSTuneHud.register();

		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.fpstune.toggle",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_F6,
				"key.category.fpstune.controls"
		));

		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
		LOGGER.info("FPS Tune initialized. F6 toggles the local render controls.");
	}

	private void onClientTick(Minecraft client) {
		while (toggleKey.consumeClick()) {
			config.enabled = !config.enabled;
			AdaptiveParticleBudgetController.reset(config);
			ConfigStore.save(client.gameDirectory.toPath(), config);
			if (client.gui != null) {
				client.gui.getChat().addMessage(Component.literal(
						"FPS Tune render controls " + (config.enabled ? "enabled" : "disabled")
				));
			}
		}
	}

	public static FPSTuneConfig config() {
		return config;
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
		int fallback = Math.max(30, Math.min(currentConfig.adaptiveTargetFps, 360));
		if (!currentConfig.adaptiveTargetAuto) {
			return fallback;
		}

		Minecraft client = Minecraft.getInstance();
		int configuredLimit = client.options.framerateLimit().get();
		return configuredLimit > 0
				? Math.max(30, Math.min(configuredLimit, 360))
				: fallback;
	}

	public static boolean isNearbyParticle(Particle particle) {
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
		if (client.player == null) {
			return false;
		}

		// Compute the existing bounding-box center directly to avoid allocating a Vec3.
		var bounds = particle.getBoundingBox();
		double centerX = (bounds.minX + bounds.maxX) * 0.5;
		double centerY = (bounds.minY + bounds.maxY) * 0.5;
		double centerZ = (bounds.minZ + bounds.maxZ) * 0.5;
		double deltaX = client.player.getX() - centerX;
		double deltaY = client.player.getY() - centerY;
		double deltaZ = client.player.getZ() - centerZ;
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ <= radiusSquared;
	}

	public static void applyConfig(Path runDirectory, FPSTuneConfig updatedConfig) {
		if (updatedConfig == null) {
			return;
		}
		updatedConfig.clamp();
		config = updatedConfig;
		AdaptiveParticleBudgetController.reset(config);
		ConfigStore.save(runDirectory, config);
	}
}
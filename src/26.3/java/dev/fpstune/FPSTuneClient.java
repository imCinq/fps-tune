package dev.fpstune;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fpstune.config.ConfigStore;
import dev.fpstune.config.FPSTuneConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.Identifier;
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

		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(MOD_ID, "controls")
		);
		toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.fpstune.toggle",
				InputConstants.Type.KEYBOARD,
				InputConstants.KEY_F6,
				category
		));

		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
		LOGGER.info("FPS Tune initialized. F6 toggles the local render controls.");
	}

	private void onClientTick(Minecraft client) {
		while (toggleKey.consumeClick()) {
			config.enabled = !config.enabled;
			AdaptiveParticleBudgetController.reset(config);
			ConfigStore.save(client.gameDirectory.toPath(), config);
			if (client.player != null) {
				FPSTuneToggleFeedback.send(config, client.player::sendOverlayMessage, client.player::sendSystemMessage);
			}
		}
	}

	public static FPSTuneConfig config() {
		return config;
	}

	public static int effectiveAdaptiveTargetFps(FPSTuneConfig currentConfig) {
		return FPSTuneClientSupport.effectiveAdaptiveTargetFps(currentConfig);
	}

	public static boolean isNearbyParticle(Particle particle) {
		return FPSTuneClientSupport.isNearbyParticle(config, particle);
	}

	public static boolean isNearbyParticle(Particle particle, double radiusSquared) {
		return FPSTuneClientSupport.isNearbyParticle(particle, radiusSquared);
	}

	public static void applyConfig(Path runDirectory, FPSTuneConfig updatedConfig) {
		if (updatedConfig != null) {
			config = FPSTuneClientSupport.commitConfig(runDirectory, updatedConfig);
		}
	}
}

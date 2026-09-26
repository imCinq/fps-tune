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
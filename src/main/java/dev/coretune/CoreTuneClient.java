package dev.coretune;

import dev.coretune.config.ConfigStore;
import dev.coretune.config.CoreTuneConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CoreTuneClient implements ClientModInitializer {
	public static final String MOD_ID = "coretune";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static CoreTuneConfig config;
	private static KeyMapping toggleKey;

	@Override
	public void onInitializeClient() {
		Minecraft client = Minecraft.getInstance();
		config = ConfigStore.load(client.gameDirectory.toPath());

		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(MOD_ID, "controls")
		);
		toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.coretune.toggle",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_F6,
				category
		));

		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
		LOGGER.info("CoreTune initialized. F6 toggles the particle admission optimization.");
	}

	private void onClientTick(Minecraft client) {
		while (toggleKey.consumeClick()) {
			config.enabled = !config.enabled;
			ConfigStore.save(client.gameDirectory.toPath(), config);
			if (client.player != null) {
				client.player.sendSystemMessage(Component.literal(
						"CoreTune particle budget " + (config.enabled ? "enabled" : "disabled")
				));
			}
		}
	}

	public static CoreTuneConfig config() {
		return config;
	}
}

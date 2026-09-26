package dev.fpstune;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fpstune.config.ConfigStore;
import dev.fpstune.config.FPSTuneConfig;
import dev.fpstune.screen.FPSTuneConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Mod(value = FPSTuneClient.MOD_ID, dist = Dist.CLIENT)
public final class FPSTuneClient {
    public static final String MOD_ID = "fpstune";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static FPSTuneConfig config;
    private static KeyMapping toggleKey;

    public FPSTuneClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(FPSTuneClient::registerKeyMappings);
        modEventBus.addListener(FPSTuneClient::registerGuiLayers);
        modEventBus.addListener(FPSTuneClient::registerReloadListeners);
        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (container, parent) -> new FPSTuneConfigScreen(parent)
        );
        NeoForge.EVENT_BUS.addListener(FPSTuneClient::onClientTick);
        LOGGER.info("FPS Tune NeoForge 1.21.1 client integration registered.");
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(
                "key.fpstune.toggle",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_F6,
                "key.category.fpstune.controls"
        );
        event.register(toggleKey);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        FPSTuneHud.register(event);
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        FPSTuneHud.registerReloadListener(event);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        ensureConfig();
        if (config == null || toggleKey == null) {
            return;
        }
        while (toggleKey.consumeClick()) {
            config.enabled = !config.enabled;
            AdaptiveParticleBudgetController.reset(config);
            ConfigStore.save(Minecraft.getInstance().gameDirectory.toPath(), config);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal(
                        "FPS Tune render controls " + (config.enabled ? "enabled" : "disabled")
                ));
            }
        }
    }

    private static void ensureConfig() {
        if (config != null) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }
        config = ConfigStore.load(client.gameDirectory.toPath());
        AdaptiveParticleBudgetController.reset(config);
    }

    public static FPSTuneConfig config() {
        ensureConfig();
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

package dev.fpstune;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fpstune.config.ConfigStore;
import dev.fpstune.config.FPSTuneConfig;
import dev.fpstune.screen.FPSTuneConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientResourceLoadFinishedEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
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
        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (container, parent) -> new FPSTuneConfigScreen(parent)
        );

        NeoForge.EVENT_BUS.addListener(FPSTuneClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(FPSTuneClient::onResourceLoadFinished);
        LOGGER.info("FPS Tune NeoForge client integration registered.");
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "controls")
        );
        event.registerCategory(category);
        toggleKey = new KeyMapping(
                "key.fpstune.toggle",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_F6,
                category
        );
        event.register(toggleKey);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        FPSTuneHud.register(event);
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

    private static void onResourceLoadFinished(ClientResourceLoadFinishedEvent event) {
        ensureConfig();
        FPSTuneHud.onResourceLoadFinished(event);
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

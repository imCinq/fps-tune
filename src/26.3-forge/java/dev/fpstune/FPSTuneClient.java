package dev.fpstune;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fpstune.config.ConfigStore;
import dev.fpstune.config.FPSTuneConfig;
import dev.fpstune.screen.FPSTuneConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Mod(FPSTuneClient.MOD_ID)
public final class FPSTuneClient {
    public static final String MOD_ID = "fpstune";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static FPSTuneConfig config;
    private static KeyMapping toggleKey;

    public FPSTuneClient(FMLJavaModLoadingContext context) {
        RegisterKeyMappingsEvent.BUS.addListener(FPSTuneClient::registerKeyMappings);
        AddGuiOverlayLayersEvent.BUS.addListener(FPSTuneHud::register);
        RegisterClientReloadListenersEvent.BUS.addListener(FPSTuneClient::registerReloadListeners);
        context.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new FPSTuneConfigScreen(parent))
        );

        TickEvent.ClientTickEvent.Post.BUS.addListener(FPSTuneClient::onClientTick);
        LOGGER.info("FPS Tune Forge client integration registered.");
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "controls")
        );
        toggleKey = new KeyMapping(
                "key.fpstune.toggle",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_F6,
                category
        );
        event.register(toggleKey);
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(FPSTuneHud.reloadListener());
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        ensureConfig();
        if (config == null || toggleKey == null) {
            return;
        }

        while (toggleKey.consumeClick()) {
            config.enabled = !config.enabled;
            AdaptiveParticleBudgetController.reset(config);
            ConfigStore.save(Minecraft.getInstance().gameDirectory.toPath(), config);
            if (Minecraft.getInstance().player != null) {
                FPSTuneToggleFeedback.send(config, Minecraft.getInstance().player::sendOverlayMessage, Minecraft.getInstance().player::sendSystemMessage);
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

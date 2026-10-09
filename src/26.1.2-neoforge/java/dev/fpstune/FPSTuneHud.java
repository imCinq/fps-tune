package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ClientResourceLoadFinishedEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public final class FPSTuneHud {
    private static final FPSTuneDiagnosticsHudCache CACHE = new FPSTuneDiagnosticsHudCache();
    private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath(
            FPSTuneClient.MOD_ID,
            "diagnostics"
    );

    private FPSTuneHud() {
    }

    public static void register(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.CHAT, HUD_ID, FPSTuneHud::extract);
    }

    public static void onResourceLoadFinished(ClientResourceLoadFinishedEvent event) {
        CACHE.invalidateWidths();
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        FPSTuneConfig config = FPSTuneClient.config();
        if (client.player == null) {
            AdaptiveParticleBudgetController.reset(config);
            return;
        }
        if (client.gui.screen() != null) {
            AdaptiveParticleBudgetController.pause();
            return;
        }
        if (FPSTuneRenderPolicy.shouldLimitParticles(config) && config.adaptiveParticleBudgetEnabled) {
            AdaptiveParticleBudgetController.observeFrame(
                    System.nanoTime(),
                    config,
                    FPSTuneClient.effectiveAdaptiveTargetFps(config),
                    ParticleAdmissionMetrics.pressureSnapshot()
            );
        }
        if (!FPSTuneDiagnostics.shouldRender(config)) {
            return;
        }

        FPSTuneDiagnosticsHudCache.View hud = CACHE.update(
                config,
                ParticleAdmissionMetrics.snapshot(),
                AdaptiveParticleBudgetController.snapshot(config),
                client.font::width
        );
        String[] lines = hud.lines();
        int x = 6;
        int y = 6;
        int lineHeight = 10;
        int width = hud.width();

        graphics.fill(x - 4, y - 4, x + width + 4, y + lines.length * lineHeight + 3, 0x90000000);
        for (int index = 0; index < lines.length; index++) {
            graphics.text(client.font, lines[index], x, y + index * lineHeight, 0xFFFFFFFF, true);
        }
    }
}

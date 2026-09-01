package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public final class FPSTuneHud {
	private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath(
			FPSTuneClient.MOD_ID,
			"diagnostics"
	);

	private FPSTuneHud() {
	}

	public static void register() {
		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, HUD_ID, FPSTuneHud::render);
	}

	private static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
		Minecraft client = Minecraft.getInstance();
		FPSTuneConfig config = FPSTuneClient.config();
		if (client.player == null || client.screen != null) {
			AdaptiveParticleBudgetController.pause(config);
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

		String[] lines = FPSTuneDiagnostics.lines(
				config,
				ParticleAdmissionMetrics.snapshot(),
				AdaptiveParticleBudgetController.snapshot(config)
		);
		int x = 6;
		int y = 6;
		int lineHeight = 10;
		int width = 0;
		for (String line : lines) {
			width = Math.max(width, client.font.width(line));
		}

		graphics.fill(x - 4, y - 4, x + width + 4, y + lines.length * lineHeight + 3, 0x90000000);
		for (int index = 0; index < lines.length; index++) {
			graphics.drawString(client.font, lines[index], x, y + index * lineHeight, 0xFFFFFFFF, true);
		}
	}
}

package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class FPSTuneHud {
	private static final FPSTuneDiagnosticsHudCache CACHE = new FPSTuneDiagnosticsHudCache();
	private FPSTuneHud() {
	}

	public static void register() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return ResourceLocation.fromNamespaceAndPath(FPSTuneClient.MOD_ID, "diagnostics_font_cache");
					}

					@Override
					public void onResourceManagerReload(ResourceManager resourceManager) {
						CACHE.invalidateWidths();
					}
				}
		);
		HudRenderCallback.EVENT.register(FPSTuneHud::render);
	}

	private static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
		Minecraft client = Minecraft.getInstance();
		FPSTuneConfig config = FPSTuneClient.config();
		if (client.player == null) {
			AdaptiveParticleBudgetController.reset(config);
			return;
		}
		if (client.screen != null) {
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
			graphics.drawString(client.font, lines[index], x, y + index * lineHeight, 0xFFFFFFFF, true);
		}
	}
}
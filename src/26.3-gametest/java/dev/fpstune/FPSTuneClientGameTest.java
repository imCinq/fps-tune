package dev.fpstune;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fpstune.config.FPSTuneConfig;
import dev.fpstune.screen.FPSTuneConfigScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.core.particles.ParticleTypes;

/** Real client integration checks, excluded from release artifacts. */
@SuppressWarnings("UnstableApiUsage")
public final class FPSTuneClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		context.runOnClient(client -> check(!FPSTuneClient.config().enabled, "fresh install must be disabled"));
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			world.getConnection().waitForChunksRender();
			context.getInput().pressKey(InputConstants.KEY_F6);
			context.runOnClient(client -> check(FPSTuneClient.config().enabled, "F6 enables controls under SDL"));
			context.getInput().pressKey(InputConstants.KEY_F6);
			context.runOnClient(client -> check(!FPSTuneClient.config().enabled, "F6 disables controls"));

			context.runOnClient(client -> {
				FPSTuneConfig config = new FPSTuneConfig();
				config.enabled = true;
				config.diagnosticsHudEnabled = true;
				config.prioritizeNearbyParticles = false;
				config.maxParticlesPerTick = 10;
				FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
				client.particleEngine.tick();
				particles(client, 25, 0);
				var metrics = ParticleAdmissionMetrics.snapshot();
				check(metrics.acceptedThisTick() == 10, "actual engine must admit exactly 10 particles");
				check(metrics.rejectedThisTick() == 15, "actual engine must reject the remaining 15");
				client.particleEngine.tick();
				particles(client, 1, 0);
				check(ParticleAdmissionMetrics.snapshot().acceptedThisTick() == 1, "tick resets admission budget");

				config = config.copy();
				config.prioritizeNearbyParticles = true;
				config.nearbyParticleReserve = 5;
				FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
				client.particleEngine.tick();
				particles(client, 10, 100);
				particles(client, 5, 0);
				metrics = ParticleAdmissionMetrics.snapshot();
				check(metrics.acceptedThisTick() == 10, "nearby reservation must preserve total limit");
				check(metrics.priorityAcceptedThisTick() == 5, "nearby particles receive reserved admissions");

				config = config.copy();
				config.weatherMode = FPSTuneConfig.WeatherMode.OFF;
				FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
				try (WeatherEffectRenderer weather = new WeatherEffectRenderer()) {
					// A null render state is deliberately a sentinel: only the injected
					// HEAD cancellation may return without vanilla dereferencing it.
					weather.render(null, null);
					for (OitStage stage : OitStage.values()) {
						weather.renderOit(stage, null, null);
					}
					config = config.copy();
					config.enabled = false;
					FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
					boolean vanillaReached = false;
					try {
						weather.render(null, null);
					} catch (NullPointerException expected) {
						vanillaReached = true;
					}
					check(vanillaReached, "master off must restore vanilla weather execution");
				}
			});

			context.setScreen(() -> new FPSTuneConfigScreen(null));
			context.takeScreenshot("fpstune-26.3-settings");
			context.clickScreenButton("button.fpstune.advanced");
			context.clickScreenButton("gui.back");
			context.clickScreenButton("gui.cancel");
			context.runOnClient(client -> {
				FPSTuneConfig config = new FPSTuneConfig();
				config.enabled = true;
				config.adaptiveParticleBudgetEnabled = true;
				config.diagnosticsHudEnabled = false;
				FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
			});
			context.waitTicks(40);
			context.runOnClient(client -> check(
					AdaptiveParticleBudgetController.snapshot(FPSTuneClient.config()).smoothedFrameTimeMillis() > 0,
					"Adaptive sampling must run with diagnostics disabled"));
			context.runOnClient(client -> {
				FPSTuneConfig config = FPSTuneClient.config().copy();
				config.diagnosticsHudEnabled = true;
				FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
			});
			context.waitTicks(5);
			context.takeScreenshot("fpstune-26.3-world-hud");
		}
		context.runOnClient(client -> FPSTuneClient.applyConfig(client.gameDirectory.toPath(), new FPSTuneConfig()));
	}

	private static void particles(Minecraft client, int count, double distance) {
		for (int index = 0; index < count; index++) {
			client.particleEngine.createParticle(ParticleTypes.FLAME,
					client.player.getX() + distance, client.player.getY() + 1, client.player.getZ(), 0, 0, 0);
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}

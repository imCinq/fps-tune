package dev.fpstune;

import com.mojang.blaze3d.platform.InputConstants;
import dev.fpstune.config.FPSTuneConfig;
import dev.fpstune.screen.FPSTuneConfigScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.particles.ParticleTypes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/** Real client integration checks, excluded from release artifacts. */
@SuppressWarnings("UnstableApiUsage")
public final class FPSTuneClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		context.runOnClient(client -> check(!FPSTuneClient.config().enabled, "fresh install must be disabled"));
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
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
				config.weatherRenderingEnabled = false;
				FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
					WeatherEffectRenderer weather = new WeatherEffectRenderer();
					Method render = weatherRenderMethod();
					render.trySetAccessible();
					// Null/default arguments are deliberate sentinels: only the injected
					// HEAD cancellation may return without vanilla dereferencing them.
					invokeWeatherRender(weather, render);
					config = config.copy();
					config.enabled = false;
					FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
					boolean vanillaReached = false;
					try {
						invokeWeatherRender(weather, render);
					} catch (NullPointerException expected) {
						vanillaReached = true;
					}
					check(vanillaReached, "master off must restore vanilla weather execution");

			});

			context.setScreen(() -> new FPSTuneConfigScreen(null));
			context.takeScreenshot("fpstune-1.21.11-settings");
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
			context.takeScreenshot("fpstune-1.21.11-world-hud");
		}
		context.runOnClient(client -> FPSTuneClient.applyConfig(client.gameDirectory.toPath(), new FPSTuneConfig()));
	}

	private static void particles(Minecraft client, int count, double distance) {
		for (int index = 0; index < count; index++) {
			client.particleEngine.createParticle(ParticleTypes.FLAME,
					client.player.getX() + distance, client.player.getY() + 1, client.player.getZ(), 0, 0, 0);
		}
	}

	private static Method weatherRenderMethod() {
		return Arrays.stream(WeatherEffectRenderer.class.getDeclaredMethods())
				.filter(method -> method.getName().equals("render") && method.getParameterCount() == 3)
				.findFirst()
				.orElseThrow(() -> new AssertionError("1.21.11 weather render target is missing"));
	}

	private static void invokeWeatherRender(WeatherEffectRenderer weather, Method render) {
		Object[] arguments = Arrays.stream(render.getParameterTypes())
				.map(FPSTuneClientGameTest::defaultValue)
				.toArray();
		try {
			render.invoke(weather, arguments);
		} catch (InvocationTargetException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof RuntimeException runtimeException) {
				throw runtimeException;
			}
			if (cause instanceof Error error) {
				throw error;
			}
			throw new AssertionError("weather render invocation failed", cause);
		} catch (ReflectiveOperationException exception) {
			throw new AssertionError("weather render reflection failed", exception);
		}
	}

	private static Object defaultValue(Class<?> type) {
		if (!type.isPrimitive()) {
			return null;
		}
		if (type == boolean.class) {
			return false;
		}
		if (type == byte.class) {
			return (byte) 0;
		}
		if (type == short.class) {
			return (short) 0;
		}
		if (type == int.class) {
			return 0;
		}
		if (type == long.class) {
			return 0L;
		}
		if (type == float.class) {
			return 0F;
		}
		if (type == double.class) {
			return 0D;
		}
		if (type == char.class) {
			return '\0';
		}
		throw new AssertionError("unsupported primitive weather argument: " + type);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}

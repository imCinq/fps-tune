package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import dev.fpstune.screen.FPSTuneConfigScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WeatherEffectRenderer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/** Real client integration checks, excluded from release artifacts. */
@SuppressWarnings("UnstableApiUsage")
public final class FPSTuneClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		context.runOnClient(client -> check(!FPSTuneClient.config().enabled, "fresh install must be disabled"));
			context.runOnClient(client -> {
				FPSTuneConfig config = new FPSTuneConfig();
				config.enabled = true;
				config.diagnosticsHudEnabled = true;
				config.prioritizeNearbyParticles = false;
				config.maxParticlesPerTick = 10;
				FPSTuneClient.applyConfig(client.gameDirectory.toPath(), config);
				client.particleEngine.tick();
				check(ParticleAdmissionMetrics.snapshot().acceptedThisTick() == 0,
						"client particle engine tick must reset admission metrics");

				config = config.copy();
				config.weatherMode = FPSTuneConfig.WeatherMode.OFF;
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
		context.runOnClient(client -> FPSTuneClient.applyConfig(client.gameDirectory.toPath(), new FPSTuneConfig()));
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

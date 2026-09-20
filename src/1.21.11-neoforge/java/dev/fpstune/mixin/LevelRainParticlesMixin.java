package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.FPSTuneRenderPolicy;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.server.level.ParticleStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public abstract class LevelRainParticlesMixin {
	@Inject(
			// The tick-time precipitation particle spawner; streaks are gated separately at render.
			method = "tickRainParticles",
			at = @At("HEAD"),
			cancellable = true,
			require = 1
	)
	private void fpstune$limitRainParticles(
			ClientLevel level,
			Camera camera,
			int ticks,
			ParticleStatus particleStatus,
			int weatherRadius,
			CallbackInfo callbackInfo
	) {
		if (!FPSTuneRenderPolicy.shouldRenderWeather(FPSTuneClient.config())) {
			callbackInfo.cancel();
		}
	}
}

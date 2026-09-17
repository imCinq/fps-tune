package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.FPSTuneRenderPolicy;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRainParticlesMixin {
	@Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
	private void fpstune$limitWeatherSplashParticles(Camera camera, CallbackInfo callbackInfo) {
		if (!FPSTuneRenderPolicy.shouldRenderWeather(FPSTuneClient.config())) {
			callbackInfo.cancel();
		}
	}
}

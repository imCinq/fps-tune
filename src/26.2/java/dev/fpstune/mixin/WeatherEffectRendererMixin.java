package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.FPSTuneRenderPolicy;
import dev.fpstune.WeatherReduction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public abstract class WeatherEffectRendererMixin {
	@Inject(
			method = "render(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void fpstune$limitWeatherRendering(
			Vec3 cameraPosition,
			WeatherRenderState renderState,
			CallbackInfo callbackInfo
	) {
		if (!FPSTuneRenderPolicy.shouldRenderWeather(FPSTuneClient.config())) {
			callbackInfo.cancel();
		}
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"), require = 1)
	private void fpstune$lightenWeather(
			ClientLevel level,
			float partialTick,
			Vec3 cameraPosition,
			WeatherRenderState renderState,
			CallbackInfo callbackInfo
	) {
		if (!WeatherReduction.active(FPSTuneClient.config())) {
			return;
		}
		int radius = WeatherReduction.reducedRadius(renderState.radius);
		renderState.rainColumns.removeIf(column -> !WeatherReduction.keepsColumn(
				column.x(), column.z(), cameraPosition.x, cameraPosition.z, radius));
		renderState.snowColumns.removeIf(column -> !WeatherReduction.keepsColumn(
				column.x(), column.z(), cameraPosition.x, cameraPosition.z, radius));
		renderState.radius = radius;
	}
}

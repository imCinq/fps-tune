package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.FPSTuneRenderPolicy;
import dev.fpstune.WeatherReduction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public abstract class WeatherEffectRendererMixin {
	@Inject(
			// Both vanilla render and renderOit delegate to this precipitation-only method.
			method = "render(Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;)V",
			at = @At("HEAD"),
			cancellable = true,
			require = 1
	)
	private void fpstune$limitWeatherRendering(
			WeatherRenderState renderState,
			RenderPass renderPass,
			RenderPipeline pipeline,
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

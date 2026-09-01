package dev.fpstune.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import dev.fpstune.FPSTuneClient;
import dev.fpstune.FPSTuneRenderPolicy;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	/**
	 * Current targets put precipitation and world-border geometry in the same
	 * weather pass. Cancel only precipitation so world-border effects remain
	 * visible when Rain and Snow is disabled.
	 */
	@Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
	private void fpstune$limitWeatherRendering(
			FrameGraphBuilder frameGraphBuilder,
			GpuBufferSlice gpuBufferSlice,
			CallbackInfo callbackInfo
	) {
		if (!FPSTuneRenderPolicy.shouldRenderWeather(FPSTuneClient.config())) {
			callbackInfo.cancel();
		}
	}
}

package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.FPSTuneRenderPolicy;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelWeatherParticlesMixin {
	@Inject(
			// The tick-time precipitation particle spawner; streaks are gated separately at render.
			method = "tickWeatherEffects",
			at = @At("HEAD"),
			cancellable = true,
			require = 1
	)
	private void fpstune$limitWeatherParticles(CallbackInfo callbackInfo) {
		if (!FPSTuneRenderPolicy.shouldRenderWeather(FPSTuneClient.config())) {
			callbackInfo.cancel();
		}
	}
}

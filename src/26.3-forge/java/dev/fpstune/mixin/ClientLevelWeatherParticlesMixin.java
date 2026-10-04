package dev.fpstune.mixin;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.FPSTuneRenderPolicy;
import dev.fpstune.WeatherReduction;
import dev.fpstune.config.FPSTuneConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class)
public abstract class ClientLevelWeatherParticlesMixin {
	@Unique
	private int fpstune$splashIndex;

	@Redirect(
			// The tick-time precipitation particle spawner; streaks are gated separately at render.
			// Redirect (not HEAD cancel) so rain sound scheduling in the same method keeps playing.
			method = "tickWeatherEffects",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
			)
	)
	private void fpstune$limitWeatherParticles(
			ClientLevel level,
			ParticleOptions particle,
			double x,
			double y,
			double z,
			double xSpeed,
			double ySpeed,
			double zSpeed
	) {
		FPSTuneConfig config = FPSTuneClient.config();
		if (!FPSTuneRenderPolicy.shouldRenderWeather(config)
				|| (WeatherReduction.active(config) && !WeatherReduction.keepsSplash(fpstune$splashIndex++))) {
			return;
		}
		level.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
	}
}

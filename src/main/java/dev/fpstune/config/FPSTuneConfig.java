package dev.fpstune.config;

public final class FPSTuneConfig {
	public static final int CURRENT_CONFIG_VERSION = 1;

	// The master switch is opt-in by default: a server should never be tested with
	// a custom client render controller enabled before its rules and staff guidance
	// have been checked.
	public boolean enabled = false;
	public boolean particleAdmissionEnabled = true;
	public int maxParticlesPerTick = 300;
	public boolean weatherRenderingEnabled = true;

	public FPSTuneConfig copy() {
		FPSTuneConfig copy = new FPSTuneConfig();
		copy.enabled = enabled;
		copy.particleAdmissionEnabled = particleAdmissionEnabled;
		copy.maxParticlesPerTick = maxParticlesPerTick;
		copy.weatherRenderingEnabled = weatherRenderingEnabled;
		return copy;
	}

	public void clamp() {
		maxParticlesPerTick = Math.max(0, Math.min(maxParticlesPerTick, 10_000));
	}
}

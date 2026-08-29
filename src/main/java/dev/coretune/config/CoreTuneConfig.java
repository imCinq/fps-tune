package dev.coretune.config;

public final class CoreTuneConfig {
	// Opt-in by default: a server should never be tested with a custom mixin enabled
	// before its rules and staff guidance have been checked.
	public boolean enabled = false;
	public int maxParticlesPerTick = 300;

	public void clamp() {
		maxParticlesPerTick = Math.max(0, Math.min(maxParticlesPerTick, 10_000));
	}
}

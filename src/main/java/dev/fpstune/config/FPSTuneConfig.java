package dev.fpstune.config;

public final class FPSTuneConfig {
	public static final int CURRENT_CONFIG_VERSION = 4;

	// The master switch is opt-in by default: a server should never be tested with
	// a custom client render controller enabled before its rules and staff guidance
	// have been checked.
	public boolean enabled = false;
	public boolean particleAdmissionEnabled = true;
	public int maxParticlesPerTick = 300;
	public boolean prioritizeNearbyParticles = true;
	public int nearbyParticleReserve = 100;
	public int nearbyParticleDistance = 16;
	public boolean diagnosticsHudEnabled = false;
	public boolean adaptiveParticleBudgetEnabled = false;
	public boolean adaptiveTargetAuto = true;
	// Manual fallback used when Auto cannot read a finite client FPS limit.
	public int adaptiveTargetFps = 120;
	public int adaptiveMinParticlesPerTick = 100;
	public int adaptiveMaxParticlesPerTick = 2_000;
	public boolean weatherRenderingEnabled = true;

	public FPSTuneConfig copy() {
		FPSTuneConfig copy = new FPSTuneConfig();
		return copy.copyFrom(this);
	}

	public FPSTuneConfig copyFrom(FPSTuneConfig source) {
		if (source == null) {
			return this;
		}

		enabled = source.enabled;
		particleAdmissionEnabled = source.particleAdmissionEnabled;
		maxParticlesPerTick = source.maxParticlesPerTick;
		prioritizeNearbyParticles = source.prioritizeNearbyParticles;
		nearbyParticleReserve = source.nearbyParticleReserve;
		nearbyParticleDistance = source.nearbyParticleDistance;
		diagnosticsHudEnabled = source.diagnosticsHudEnabled;
		adaptiveParticleBudgetEnabled = source.adaptiveParticleBudgetEnabled;
		adaptiveTargetAuto = source.adaptiveTargetAuto;
		adaptiveTargetFps = source.adaptiveTargetFps;
		adaptiveMinParticlesPerTick = source.adaptiveMinParticlesPerTick;
		adaptiveMaxParticlesPerTick = source.adaptiveMaxParticlesPerTick;
		weatherRenderingEnabled = source.weatherRenderingEnabled;
		clamp();
		return this;
	}

	/**
	 * Restores only the settings owned by the Advanced screen. Basic-screen
	 * choices remain in the draft until the user explicitly changes them.
	 */
	public void resetAdvancedSettings() {
		FPSTuneConfig defaults = new FPSTuneConfig();
		particleAdmissionEnabled = defaults.particleAdmissionEnabled;
		maxParticlesPerTick = defaults.maxParticlesPerTick;
		prioritizeNearbyParticles = defaults.prioritizeNearbyParticles;
		nearbyParticleReserve = defaults.nearbyParticleReserve;
		nearbyParticleDistance = defaults.nearbyParticleDistance;
		adaptiveParticleBudgetEnabled = defaults.adaptiveParticleBudgetEnabled;
		adaptiveTargetAuto = defaults.adaptiveTargetAuto;
		adaptiveTargetFps = defaults.adaptiveTargetFps;
		adaptiveMinParticlesPerTick = defaults.adaptiveMinParticlesPerTick;
		adaptiveMaxParticlesPerTick = defaults.adaptiveMaxParticlesPerTick;
		clamp();
	}

	public void clamp() {
		maxParticlesPerTick = Math.max(0, Math.min(maxParticlesPerTick, 10_000));
		nearbyParticleReserve = Math.max(0, Math.min(nearbyParticleReserve, 10_000));
		nearbyParticleDistance = Math.max(0, Math.min(nearbyParticleDistance, 64));
		adaptiveTargetFps = Math.max(30, Math.min(adaptiveTargetFps, 360));
		adaptiveMinParticlesPerTick = Math.max(0, Math.min(adaptiveMinParticlesPerTick, 10_000));
		adaptiveMaxParticlesPerTick = Math.max(0, Math.min(adaptiveMaxParticlesPerTick, 10_000));
		if (adaptiveMinParticlesPerTick > adaptiveMaxParticlesPerTick) {
			adaptiveMaxParticlesPerTick = adaptiveMinParticlesPerTick;
		}
	}
}

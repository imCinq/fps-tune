package dev.fpstune;

import net.minecraft.client.particle.ParticleGroup;

/** Reads the size of one of the particle engine's render groups. */
public final class ParticleCounts {
	private ParticleCounts() {
	}

	public static int size(Object group) {
		return group instanceof ParticleGroup<?> particleGroup ? particleGroup.size() : 0;
	}
}

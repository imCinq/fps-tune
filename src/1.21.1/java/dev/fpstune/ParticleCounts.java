package dev.fpstune;

import java.util.Collection;

/** Reads the size of one of the particle engine's render queues. */
public final class ParticleCounts {
	private ParticleCounts() {
	}

	public static int size(Object group) {
		return group instanceof Collection<?> particles ? particles.size() : 0;
	}
}

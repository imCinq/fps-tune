package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

final class FPSTuneDiagnosticsHudCacheTest {
	@Test
	void reusesLinesAndWidthsUntilADisplayedValueChanges() {
		FPSTuneDiagnosticsHudCache cache = new FPSTuneDiagnosticsHudCache();
		FPSTuneConfig config = diagnosticsConfig();
		AtomicInteger widthCalls = new AtomicInteger();
		ToIntFunction<String> widthOf = line -> {
			widthCalls.incrementAndGet();
			return line.length();
		};
		AdaptiveParticleBudgetController.Snapshot adaptive = adaptiveSnapshot();

		FPSTuneDiagnosticsHudCache.View first = cache.update(
				config,
				new ParticleAdmissionMetrics.Snapshot(3, 2, 0, 0),
				adaptive,
				widthOf
		);
		FPSTuneDiagnosticsHudCache.View unchanged = cache.update(
				config,
				new ParticleAdmissionMetrics.Snapshot(3, 2, 0, 0),
				adaptive,
				widthOf
		);

		assertSame(first.lines(), unchanged.lines());
		assertEquals(6, widthCalls.get());

		FPSTuneDiagnosticsHudCache.View changed = cache.update(
				config,
				new ParticleAdmissionMetrics.Snapshot(4, 2, 0, 0),
				adaptive,
				widthOf
		);

		assertNotSame(first.lines(), changed.lines());
		assertEquals(12, widthCalls.get());
	}

	@Test
	void ignoresValuesThatAreNotCurrentlyDisplayed() {
		FPSTuneDiagnosticsHudCache cache = new FPSTuneDiagnosticsHudCache();
		FPSTuneConfig config = diagnosticsConfig();
		config.prioritizeNearbyParticles = false;
		AtomicInteger widthCalls = new AtomicInteger();

		FPSTuneDiagnosticsHudCache.View first = cache.update(
				config,
				new ParticleAdmissionMetrics.Snapshot(3, 2, 0, 0),
				adaptiveSnapshot(),
				line -> {
					widthCalls.incrementAndGet();
					return line.length();
				}
		);
		config.nearbyParticleReserve = 999;
		FPSTuneDiagnosticsHudCache.View unchanged = cache.update(
				config,
				new ParticleAdmissionMetrics.Snapshot(3, 2, 7, 4),
				adaptiveSnapshot(),
				line -> {
					widthCalls.incrementAndGet();
					return line.length();
				}
		);

		assertSame(first.lines(), unchanged.lines());
		assertEquals(6, widthCalls.get());
	}

	@Test
	void resourceReloadInvalidatesWidthsWithoutRebuildingLines() {
		FPSTuneDiagnosticsHudCache cache = new FPSTuneDiagnosticsHudCache();
		FPSTuneConfig config = diagnosticsConfig();
		AtomicInteger widthCalls = new AtomicInteger();
		ToIntFunction<String> widthOf = line -> {
			widthCalls.incrementAndGet();
			return line.length();
		};

		FPSTuneDiagnosticsHudCache.View first = cache.update(
				config,
				new ParticleAdmissionMetrics.Snapshot(3, 2, 0, 0),
				adaptiveSnapshot(),
				widthOf
		);
		cache.invalidateWidths();
		FPSTuneDiagnosticsHudCache.View afterReload = cache.update(
				config,
				new ParticleAdmissionMetrics.Snapshot(3, 2, 0, 0),
				adaptiveSnapshot(),
				widthOf
		);

		assertSame(first.lines(), afterReload.lines());
		assertEquals(12, widthCalls.get());
	}

	private static FPSTuneConfig diagnosticsConfig() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.enabled = true;
		config.particleAdmissionEnabled = true;
		config.diagnosticsHudEnabled = true;
		return config;
	}

	private static AdaptiveParticleBudgetController.Snapshot adaptiveSnapshot() {
		return new AdaptiveParticleBudgetController.Snapshot(
				300,
				300,
				300,
				120,
				-1.0,
				AdaptiveParticleBudgetController.Direction.FIXED
		);
	}
}

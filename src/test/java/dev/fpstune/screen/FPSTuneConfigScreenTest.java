package dev.fpstune.screen;

import dev.fpstune.config.FPSTuneConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneConfigScreenTest {
	@Test
	void defaultSettingsUseTheBalancedProfile() {
		assertEquals(
				FPSTuneConfigScreen.PerformanceProfile.BALANCED,
				FPSTuneConfigScreen.profileFor(new FPSTuneConfig())
		);
	}

	@Test
	void selectingASmootherFramesProfileEnablesTheBoundedAutomaticLimit() {
		FPSTuneConfig config = new FPSTuneConfig();

		FPSTuneConfigScreen.applyProfile(
				config,
				FPSTuneConfigScreen.PerformanceProfile.SMOOTHER_FRAMES
		);

		assertTrue(config.particleAdmissionEnabled);
		assertEquals(150, config.maxParticlesPerTick);
		assertTrue(config.adaptiveParticleBudgetEnabled);
		assertEquals(120, config.adaptiveTargetFps);
		assertEquals(100, config.adaptiveMinParticlesPerTick);
		assertEquals(300, config.adaptiveMaxParticlesPerTick);
		assertEquals(
				FPSTuneConfigScreen.PerformanceProfile.SMOOTHER_FRAMES,
				FPSTuneConfigScreen.profileFor(config)
		);
	}

	@Test
	void changingAnAdvancedValueMakesTheProfileCustom() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.maxParticlesPerTick = 301;

		assertEquals(
				FPSTuneConfigScreen.PerformanceProfile.CUSTOM,
				FPSTuneConfigScreen.profileFor(config)
		);
	}

	@Test
	void choosingCustomLeavesTheExistingDraftUntouched() {
		FPSTuneConfig config = new FPSTuneConfig();
		config.maxParticlesPerTick = 512;
		config.adaptiveParticleBudgetEnabled = true;

		FPSTuneConfigScreen.applyProfile(
				config,
				FPSTuneConfigScreen.PerformanceProfile.CUSTOM
		);

		assertEquals(512, config.maxParticlesPerTick);
		assertTrue(config.adaptiveParticleBudgetEnabled);
		assertFalse(config.enabled);
	}
	@Test
	void iconFontDoesNotLeakOntoItsLabel() {
		for (Component component : new Component[]{
				FPSTuneSettingsIcons.precipitation(Component.literal("Weather")),
				FPSTuneSettingsIcons.profile(Component.literal("Profile")),
				FPSTuneSettingsIcons.overlay(Component.literal("Performance overlay"))
		}) {
			assertEquals(Style.EMPTY, component.getStyle(), "the wrapper must not set the icon-only font");
			assertEquals(3, component.getSiblings().size());

			assertTrue(
					component.getSiblings().get(0).getStyle().getFont() instanceof FontDescription.Resource,
					"the glyph uses the icon font"
			);
			assertEquals(Style.EMPTY, component.getSiblings().get(1).getStyle(), "the spacer uses the normal font");
			assertEquals(Style.EMPTY, component.getSiblings().get(2).getStyle(), "the label uses the normal font");
		}
	}

}

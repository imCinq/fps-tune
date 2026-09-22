package dev.fpstune.screen;

import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

final class FPSTuneSettingsIconsTest {
	@Test
	void iconFontDoesNotLeakOntoItsLabel() {
		for (Component component : new Component[]{
				FPSTuneSettingsIcons.precipitation(Component.literal("Weather")),
				FPSTuneSettingsIcons.profile(Component.literal("Profile")),
				FPSTuneSettingsIcons.overlay(Component.literal("Performance overlay"))
		}) {
			assertNull(component.getStyle().getFont(), "the wrapper must not set the icon-only font");
			assertEquals(3, component.getSiblings().size());

			assertNotNull(component.getSiblings().get(0).getStyle().getFont(), "the glyph uses the icon font");
			assertNull(component.getSiblings().get(1).getStyle().getFont(), "the spacer uses the normal font");
			assertNull(component.getSiblings().get(2).getStyle().getFont(), "the label uses the normal font");
		}
	}
}

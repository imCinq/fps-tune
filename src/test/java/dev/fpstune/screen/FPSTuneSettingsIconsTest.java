package dev.fpstune.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Identifier;
import net.minecraft.network.chat.Style;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneSettingsIconsTest {
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

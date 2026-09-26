package dev.fpstune.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

final class FPSTuneSettingsIcons {
	private static final Identifier FONT = Identifier.fromNamespaceAndPath("fpstune", "settings_icons");

	private FPSTuneSettingsIcons() {
	}

	static Component precipitation(Component label) {
		return withIcon("\uE000", 0x68C9E8, label);
	}

	static Component profile(Component label) {
		return withIcon("\uE001", 0xB7C7F2, label);
	}

	static Component overlay(Component label) {
		return withIcon("\uE002", 0x85DDA5, label);
	}

	static Component power(Component label) {
		return withIcon("\uE003", 0xF2C66D, label);
	}

	static Component particles(Component label) {
		return withIcon("\uE004", 0xF5A3C7, label);
	}

	static Component display(Component label) {
		return withIcon("\uE005", 0x85DDA5, label);
	}

	static Component advanced(Component label) {
		return withIcon("\uE006", 0xC8C8C8, label);
	}

	static Component message(Component label) {
		return withIcon("\uE007", 0xE8D08A, label);
	}

	private static Component withIcon(String glyph, int color, Component label) {
		return Component.empty()
				.append(Component.literal(glyph)
						.withStyle(Style.EMPTY.withFont(new FontDescription.Resource(FONT)).withColor(TextColor.fromRgb(color))))
				.append(Component.literal(" "))
				.append(label);
	}
}

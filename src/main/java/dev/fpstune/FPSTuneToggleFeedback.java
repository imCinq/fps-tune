package dev.fpstune;

import dev.fpstune.config.FPSTuneConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

/**
 * Builds the F6 message and sends it where the player asked. Each target passes
 * its own action bar and chat outputs because those APIs differ by version.
 */
public final class FPSTuneToggleFeedback {
	private FPSTuneToggleFeedback() {
	}

	public static void send(FPSTuneConfig config, Consumer<Component> actionBar, Consumer<Component> chat) {
		switch (config.toggleFeedback) {
			case ACTION_BAR -> actionBar.accept(message(config));
			case CHAT -> chat.accept(message(config));
			case NONE -> {
			}
		}
	}

	public static Component message(FPSTuneConfig config) {
		MutableComponent message = Component.translatable("message.fpstune.name")
				.append(" ")
				.append(Component.translatable(config.enabled ? "message.fpstune.on" : "message.fpstune.off")
						.withStyle(config.enabled ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD));
		for (String key : FPSTuneToggleSummary.keys(config)) {
			message.append(Component.literal(" · ").withStyle(ChatFormatting.DARK_GRAY))
					.append(Component.translatable(key).withStyle(ChatFormatting.GRAY));
		}
		return message;
	}
}

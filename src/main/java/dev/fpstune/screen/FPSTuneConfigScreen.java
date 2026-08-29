package dev.fpstune.screen;

import dev.fpstune.FPSTuneClient;
import dev.fpstune.config.FPSTuneConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class FPSTuneConfigScreen extends Screen {
	private static final int CONTENT_WIDTH = 320;
	private static final int BUTTON_WIDTH = 120;
	private static final List<Integer> PARTICLE_BUDGET_PRESETS = List.of(0, 100, 300, 600, 1_200, 10_000);

	private final Screen parent;
	private final FPSTuneConfig draftConfig;

	public FPSTuneConfigScreen(Screen parent) {
		super(Component.translatable("screen.fpstune.title"));
		this.parent = parent;
		FPSTuneConfig currentConfig = FPSTuneClient.config();
		this.draftConfig = currentConfig == null ? new FPSTuneConfig() : currentConfig.copy();
		this.draftConfig.clamp();
	}

	@Override
	protected void init() {
		int contentWidth = Math.min(CONTENT_WIDTH, Math.max(160, width - 40));
		int left = (width - contentWidth) / 2;

		int titleWidth = font.width(getTitle());
		addRenderableOnly(new StringWidget(
				(width - titleWidth) / 2,
				16,
				titleWidth,
				12,
				getTitle(),
				font
		));

		MultiLineTextWidget description = new MultiLineTextWidget(
				left,
				34,
				Component.translatable("screen.fpstune.description"),
				font
		).setMaxWidth(contentWidth).setCentered(true);
		addRenderableOnly(description);

		int y = 72;
		Checkbox enabled = addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.enabled"),
				font
		).pos(left, y).maxWidth(contentWidth).selected(draftConfig.enabled).onValueChange(
				(checkbox, value) -> draftConfig.enabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.enabled.tooltip"))).build());

		y += 26;
		addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.particle_admission"),
				font
		).pos(left, y).maxWidth(contentWidth).selected(draftConfig.particleAdmissionEnabled).onValueChange(
				(checkbox, value) -> draftConfig.particleAdmissionEnabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.particle_admission.tooltip"))).build());

		y += 26;
		addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.weather_rendering"),
				font
		).pos(left, y).maxWidth(contentWidth).selected(draftConfig.weatherRenderingEnabled).onValueChange(
				(checkbox, value) -> draftConfig.weatherRenderingEnabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.weather_rendering.tooltip"))).build());

		y += 28;
		List<Integer> budgetValues = new ArrayList<>(PARTICLE_BUDGET_PRESETS);
		if (!budgetValues.contains(draftConfig.maxParticlesPerTick)) {
			budgetValues.add(draftConfig.maxParticlesPerTick);
			budgetValues.sort(Integer::compareTo);
		}
		addRenderableWidget(CycleButton.<Integer>builder(
				FPSTuneConfigScreen::formatParticleBudget,
				draftConfig.maxParticlesPerTick
		).withValues(budgetValues).create(
				left,
				y,
				contentWidth,
				20,
				Component.translatable("option.fpstune.max_particles"),
				(button, value) -> draftConfig.maxParticlesPerTick = value
		));

		int buttonY = height - 28;
		addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> closeWithoutSaving())
				.bounds(left, buttonY, BUTTON_WIDTH, 20)
				.build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> saveAndClose())
				.bounds(width - left - BUTTON_WIDTH, buttonY, BUTTON_WIDTH, 20)
				.build());

		setInitialFocus(enabled);
	}

	@Override
	public void onClose() {
		closeWithoutSaving();
	}

	private void closeWithoutSaving() {
		minecraft.setScreenAndShow(parent);
	}

	private void saveAndClose() {
		FPSTuneClient.applyConfig(minecraft.gameDirectory.toPath(), draftConfig);
		minecraft.setScreenAndShow(parent);
	}

	private static Component formatParticleBudget(Integer budget) {
		return Component.translatable("option.fpstune.max_particles.value", budget);
	}
}

package dev.fpstune.screen;

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

public final class FPSTuneAdvancedConfigScreen extends Screen {
	private static final List<Integer> PARTICLE_LIMIT_PRESETS = List.of(0, 100, 300, 600, 1_200, 10_000);
	private static final List<Integer> NEARBY_PROTECTION_PRESETS = List.of(0, 50, 100, 150, 200, 300);
	private static final List<Integer> NEARBY_RANGE_PRESETS = List.of(4, 8, 16, 24, 32);
	private static final List<Integer> TARGET_FPS_PRESETS = List.of(60, 90, 120, 144, 165, 240);
	private static final List<Integer> MINIMUM_LIMIT_PRESETS = List.of(0, 50, 100, 200, 300);
	private static final List<Integer> MAXIMUM_LIMIT_PRESETS = List.of(300, 600, 1_000, 2_000, 4_000, 10_000);

	private final FPSTuneConfigScreen parent;
	private final FPSTuneConfig draftConfig;

	public FPSTuneAdvancedConfigScreen(FPSTuneConfigScreen parent, FPSTuneConfig draftConfig) {
		super(Component.translatable("screen.fpstune.advanced.title"));
		this.parent = parent;
		this.draftConfig = draftConfig;
	}

	@Override
	protected void init() {
		FPSTuneConfigLayout.AdvancedLayout layout = FPSTuneConfigLayout.calculateAdvanced(width, height);
		int left = layout.left();
		int right = layout.right();
		int columnWidth = layout.columnWidth();

		addRenderableOnly(new StringWidget(
				(width - font.width(getTitle())) / 2,
				4,
				font.width(getTitle()),
				12,
				getTitle(),
				font
		));
		addRenderableOnly(new MultiLineTextWidget(
				left,
				FPSTuneConfigLayout.descriptionY(),
				Component.translatable("screen.fpstune.advanced.description"),
				font
		).setMaxWidth(layout.contentWidth()).setCentered(true));

		addRenderableOnly(new StringWidget(
				left,
				layout.top(),
				columnWidth,
				layout.headingHeight(),
				Component.translatable("section.fpstune.particle_controls"),
				font
		));
		addRenderableOnly(new StringWidget(
				right,
				layout.top(),
				columnWidth,
				layout.headingHeight(),
				Component.translatable("section.fpstune.automatic_controls"),
				font
		));

		Checkbox particleAdmission = addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.particle_admission"),
				font
		).pos(left, layout.firstControlY()).maxWidth(columnWidth).selected(draftConfig.particleAdmissionEnabled).onValueChange(
				(checkbox, value) -> draftConfig.particleAdmissionEnabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.particle_admission.tooltip"))).build());

		addRenderableWidget(CycleButton.<Integer>builder(
				FPSTuneAdvancedConfigScreen::formatParticleLimit
		).withInitialValue(draftConfig.maxParticlesPerTick).withValues(withCurrentValue(PARTICLE_LIMIT_PRESETS, draftConfig.maxParticlesPerTick)).create(
				left,
				layout.secondControlY(),
				columnWidth,
				20,
				Component.translatable("option.fpstune.max_particles"),
				(button, value) -> draftConfig.maxParticlesPerTick = value
		));

		addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.nearby_priority"),
				font
		).pos(left, layout.thirdControlY()).maxWidth(columnWidth).selected(draftConfig.prioritizeNearbyParticles).onValueChange(
				(checkbox, value) -> draftConfig.prioritizeNearbyParticles = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.nearby_priority.tooltip"))).build());

		addRenderableWidget(CycleButton.<Integer>builder(
				FPSTuneAdvancedConfigScreen::formatNearbyProtection
		).withInitialValue(draftConfig.nearbyParticleReserve).withValues(withCurrentValue(NEARBY_PROTECTION_PRESETS, draftConfig.nearbyParticleReserve)).create(
				left,
				layout.fourthControlY(),
				columnWidth,
				20,
				Component.translatable("option.fpstune.nearby_reserve"),
				(button, value) -> draftConfig.nearbyParticleReserve = value
		));

		addRenderableWidget(CycleButton.<Integer>builder(
				FPSTuneAdvancedConfigScreen::formatNearbyRange
		).withInitialValue(draftConfig.nearbyParticleDistance).withValues(withCurrentValue(NEARBY_RANGE_PRESETS, draftConfig.nearbyParticleDistance)).create(
				left,
				layout.fifthControlY(),
				columnWidth,
				20,
				Component.translatable("option.fpstune.nearby_distance"),
				(button, value) -> draftConfig.nearbyParticleDistance = value
		));

		addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.adaptive_budget"),
				font
		).pos(right, layout.firstControlY()).maxWidth(columnWidth).selected(draftConfig.adaptiveParticleBudgetEnabled).onValueChange(
				(checkbox, value) -> draftConfig.adaptiveParticleBudgetEnabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.adaptive_budget.tooltip"))).build());

		addRenderableWidget(CycleButton.<Integer>builder(
				FPSTuneAdvancedConfigScreen::formatTargetFps
		).withInitialValue(draftConfig.adaptiveTargetFps).withValues(withCurrentValue(TARGET_FPS_PRESETS, draftConfig.adaptiveTargetFps)).create(
				right,
				layout.secondControlY(),
				columnWidth,
				20,
				Component.translatable("option.fpstune.adaptive_target"),
				(button, value) -> draftConfig.adaptiveTargetFps = value
		));

		addRenderableWidget(CycleButton.<Integer>builder(
				FPSTuneAdvancedConfigScreen::formatMinimumLimit
		).withInitialValue(draftConfig.adaptiveMinParticlesPerTick).withValues(withCurrentValue(MINIMUM_LIMIT_PRESETS, draftConfig.adaptiveMinParticlesPerTick)).create(
				right,
				layout.thirdControlY(),
				columnWidth,
				20,
				Component.translatable("option.fpstune.adaptive_minimum"),
				(button, value) -> draftConfig.adaptiveMinParticlesPerTick = value
		));

		addRenderableWidget(CycleButton.<Integer>builder(
				FPSTuneAdvancedConfigScreen::formatMaximumLimit
		).withInitialValue(draftConfig.adaptiveMaxParticlesPerTick).withValues(withCurrentValue(MAXIMUM_LIMIT_PRESETS, draftConfig.adaptiveMaxParticlesPerTick)).create(
				right,
				layout.fourthControlY(),
				columnWidth,
				20,
				Component.translatable("option.fpstune.adaptive_maximum"),
				(button, value) -> draftConfig.adaptiveMaxParticlesPerTick = value
		));

		addRenderableWidget(Button.builder(
				Component.translatable("button.fpstune.reset"),
				button -> resetDefaults()
		).bounds(left, layout.resetY(), layout.resetButtonWidth(), 20)
				.tooltip(Tooltip.create(Component.translatable("button.fpstune.reset.tooltip")))
				.build());

		addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> returnToMain())
				.bounds(left, layout.buttonY(), FPSTuneConfigLayout.BUTTON_WIDTH, 20)
				.build());

		setInitialFocus(particleAdmission);
	}

	private void resetDefaults() {
		draftConfig.copyFrom(new FPSTuneConfig());
		rebuildWidgets();
	}

	private void returnToMain() {
		minecraft.setScreen(parent);
	}

	@Override
	public void onClose() {
		returnToMain();
	}

	private static List<Integer> withCurrentValue(List<Integer> presets, int currentValue) {
		if (presets.contains(currentValue)) {
			return presets;
		}
		List<Integer> values = new ArrayList<>(presets);
		values.add(currentValue);
		values.sort(Integer::compareTo);
		return values;
	}

	private static Component formatParticleLimit(Integer limit) {
		return Component.translatable("option.fpstune.max_particles.value", limit);
	}

	private static Component formatNearbyProtection(Integer protection) {
		return Component.translatable("option.fpstune.nearby_reserve.value", protection);
	}

	private static Component formatNearbyRange(Integer range) {
		return Component.translatable("option.fpstune.nearby_distance.value", range);
	}

	private static Component formatTargetFps(Integer targetFps) {
		return Component.translatable("option.fpstune.adaptive_target.value", targetFps);
	}

	private static Component formatMinimumLimit(Integer minimum) {
		return Component.translatable("option.fpstune.adaptive_minimum.value", minimum);
	}

	private static Component formatMaximumLimit(Integer maximum) {
		return Component.translatable("option.fpstune.adaptive_maximum.value", maximum);
	}
}

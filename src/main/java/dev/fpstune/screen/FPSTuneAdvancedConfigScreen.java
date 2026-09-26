package dev.fpstune.screen;

import dev.fpstune.config.FPSTuneConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.IntConsumer;

public final class FPSTuneAdvancedConfigScreen extends Screen {
	private static final List<Integer> PARTICLE_LIMIT_PRESETS = List.of(0, 100, 300, 600, 1_200, 10_000);
	private static final List<Integer> NEARBY_PROTECTION_PRESETS = List.of(0, 50, 100, 150, 200, 300);
	private static final List<Integer> NEARBY_RANGE_PRESETS = List.of(4, 8, 16, 24, 32);
	private static final List<Integer> TARGET_FPS_PRESETS = List.of(0, 60, 90, 120, 144, 165, 240);
	private static final List<Integer> MINIMUM_LIMIT_PRESETS = List.of(0, 50, 100, 200, 300);
	private static final List<Integer> MAXIMUM_LIMIT_PRESETS = List.of(300, 600, 1_000, 2_000, 4_000, 10_000);

	private final FPSTuneConfigScreen parent;
	private final FPSTuneConfig draftConfig;
	private boolean showMore;
	private int selectedTab;
	private TabManager tabManager;
	private List<Tab> tabs = List.of();
	private Checkbox particleAdmissionWidget;
	private CycleButton<Integer> maxParticlesWidget;
	private Checkbox adaptiveBudgetWidget;
	private Checkbox nearbyPriorityWidget;
	private CycleButton<Integer> nearbyReserveWidget;
	private CycleButton<Integer> nearbyRangeWidget;
	private CycleButton<Integer> targetFpsWidget;
	private CycleButton<Integer> minimumLimitWidget;
	private CycleButton<Integer> maximumLimitWidget;

	public FPSTuneAdvancedConfigScreen(FPSTuneConfigScreen parent, FPSTuneConfig draftConfig) {
		super(Component.translatable("screen.fpstune.advanced.title"));
		this.parent = parent;
		this.draftConfig = draftConfig;
		// Open the fine-tuning rows straight away if the player already changed one of them.
		this.showMore = hasCustomizedFineTuning(draftConfig);
	}

	@Override
	protected void init() {
		if (tabManager != null) {
			selectedTab = Math.max(0, tabs.indexOf(tabManager.getCurrentTab()));
		}

		FPSTuneSettingsLayout.Advanced layout = FPSTuneSettingsLayout.advanced(width, height);
		tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);
		tabs = List.of(particlesTab(layout), weatherTab(layout), displayTab(layout));
		TabNavigationBar tabBar = FPSTuneTabBars.create(tabManager, width, tabs.toArray(new Tab[0]));
		addRenderableWidget(tabBar);

		addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> returnToMain())
				.bounds(layout.backX(), layout.backY(), layout.backWidth(), 20)
				.build());

		tabManager.setTabArea(new ScreenRectangle(
				0,
				FPSTuneSettingsLayout.TAB_BAR_HEIGHT,
				width,
				Math.max(0, layout.backY() - FPSTuneSettingsLayout.TAB_BAR_HEIGHT)
		));
		tabBar.selectTab(Math.min(selectedTab, tabs.size() - 1), false);
		updateWidgetStates();
	}

	private Tab particlesTab(FPSTuneSettingsLayout.Advanced layout) {
		SettingsTab tab = new SettingsTab(FPSTuneSettingsIcons.particles(Component.translatable("tab.fpstune.particles")));

		particleAdmissionWidget = tab.add(Checkbox.builder(
				FPSTuneSettingsIcons.particles(Component.translatable("option.fpstune.particle_admission")),
				font
		).pos(layout.left(), layout.rowY(0)).maxWidth(layout.columnWidth()).selected(draftConfig.particleAdmissionEnabled).onValueChange(
				(checkbox, value) -> {
					draftConfig.particleAdmissionEnabled = value;
					updateWidgetStates();
				}
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.particle_admission.tooltip"))).build());

		maxParticlesWidget = tab.add(cycle(
				layout, 0, 1,
				PARTICLE_LIMIT_PRESETS,
				draftConfig.maxParticlesPerTick,
				"option.fpstune.max_particles",
				value -> Component.translatable("option.fpstune.max_particles.value", value),
				value -> draftConfig.maxParticlesPerTick = value
		));

		adaptiveBudgetWidget = tab.add(Checkbox.builder(
				Component.translatable("option.fpstune.adaptive_budget"),
				font
		).pos(layout.right(), layout.rowY(0)).maxWidth(layout.columnWidth()).selected(draftConfig.adaptiveParticleBudgetEnabled).onValueChange(
				(checkbox, value) -> {
					draftConfig.adaptiveParticleBudgetEnabled = value;
					updateWidgetStates();
				}
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.adaptive_budget.tooltip"))).build());

		int buttonRow = 2;
		if (showMore) {
			nearbyPriorityWidget = tab.add(Checkbox.builder(
					Component.translatable("option.fpstune.nearby_priority"),
					font
			).pos(layout.left(), layout.rowY(2)).maxWidth(layout.columnWidth()).selected(draftConfig.prioritizeNearbyParticles).onValueChange(
					(checkbox, value) -> {
						draftConfig.prioritizeNearbyParticles = value;
						updateWidgetStates();
					}
			).tooltip(Tooltip.create(Component.translatable("option.fpstune.nearby_priority.tooltip"))).build());
			nearbyReserveWidget = tab.add(cycle(
					layout, 0, 3,
					NEARBY_PROTECTION_PRESETS,
					draftConfig.nearbyParticleReserve,
					"option.fpstune.nearby_reserve",
					value -> Component.translatable("option.fpstune.nearby_reserve.value", value),
					value -> draftConfig.nearbyParticleReserve = value
			));
			nearbyRangeWidget = tab.add(cycle(
					layout, 0, 4,
					NEARBY_RANGE_PRESETS,
					draftConfig.nearbyParticleDistance,
					"option.fpstune.nearby_distance",
					value -> Component.translatable("option.fpstune.nearby_distance.value", value),
					value -> draftConfig.nearbyParticleDistance = value
			));

			int targetPreset = draftConfig.adaptiveTargetAuto ? 0 : draftConfig.adaptiveTargetFps;
			targetFpsWidget = tab.add(cycle(
					layout, 1, 1,
					TARGET_FPS_PRESETS,
					targetPreset,
					"option.fpstune.adaptive_target",
					value -> value == 0
							? Component.translatable("option.fpstune.adaptive_target.auto")
							: Component.translatable("option.fpstune.adaptive_target.value", value),
					value -> {
						if (value == 0) {
							draftConfig.adaptiveTargetAuto = true;
						} else {
							draftConfig.adaptiveTargetAuto = false;
							draftConfig.adaptiveTargetFps = value;
						}
					}
			));
			minimumLimitWidget = tab.add(cycle(
					layout, 1, 2,
					MINIMUM_LIMIT_PRESETS,
					draftConfig.adaptiveMinParticlesPerTick,
					"option.fpstune.adaptive_minimum",
					value -> Component.translatable("option.fpstune.adaptive_minimum.value", value),
					value -> draftConfig.adaptiveMinParticlesPerTick = value
			));
			maximumLimitWidget = tab.add(cycle(
					layout, 1, 3,
					MAXIMUM_LIMIT_PRESETS,
					draftConfig.adaptiveMaxParticlesPerTick,
					"option.fpstune.adaptive_maximum",
					value -> Component.translatable("option.fpstune.adaptive_maximum.value", value),
					value -> draftConfig.adaptiveMaxParticlesPerTick = value
			));
			buttonRow = 5;
		} else {
			nearbyPriorityWidget = null;
			nearbyReserveWidget = null;
			nearbyRangeWidget = null;
			targetFpsWidget = null;
			minimumLimitWidget = null;
			maximumLimitWidget = null;
		}

		tab.add(Button.builder(
				Component.translatable(showMore ? "button.fpstune.show_less" : "button.fpstune.show_more"),
				button -> {
					showMore = !showMore;
					rebuildWidgets();
				}
		).bounds(layout.left(), layout.rowY(buttonRow), layout.columnWidth(), 20)
				.tooltip(Tooltip.create(Component.translatable("button.fpstune.show_more.tooltip")))
				.build());
		tab.add(resetButton(layout, buttonRow, draftConfig::resetAdvancedSettings));
		return tab;
	}

	private Tab weatherTab(FPSTuneSettingsLayout.Advanced layout) {
		SettingsTab tab = new SettingsTab(FPSTuneSettingsIcons.precipitation(Component.translatable("tab.fpstune.weather")));
		tab.add(FPSTuneConfigScreen.weatherButton(draftConfig, layout.left(), layout.rowY(0), layout.width()));
		tab.add(helpText(layout, 1, "tab.fpstune.weather.help"));
		tab.add(resetButton(layout, 3, () -> draftConfig.weatherMode = FPSTuneConfig.WeatherMode.VANILLA));
		return tab;
	}

	private Tab displayTab(FPSTuneSettingsLayout.Advanced layout) {
		SettingsTab tab = new SettingsTab(FPSTuneSettingsIcons.display(Component.translatable("tab.fpstune.display")));
		tab.add(Checkbox.builder(
				FPSTuneSettingsIcons.overlay(Component.translatable("option.fpstune.diagnostics_hud")),
				font
		).pos(layout.left(), layout.rowY(0)).maxWidth(layout.width()).selected(draftConfig.diagnosticsHudEnabled).onValueChange(
				(checkbox, value) -> draftConfig.diagnosticsHudEnabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.diagnostics_hud.tooltip"))).build());
		CycleButton<FPSTuneConfig.ToggleFeedback> feedback = tab.add(CycleButton.<FPSTuneConfig.ToggleFeedback>builder(
				value -> Component.translatable("option.fpstune.toggle_feedback." + value.name().toLowerCase(Locale.ROOT)),
				draftConfig.toggleFeedback
		).withValues(FPSTuneConfig.ToggleFeedback.values()).create(
				layout.left(),
				layout.rowY(1),
				layout.width(),
				20,
				FPSTuneSettingsIcons.message(Component.translatable("option.fpstune.toggle_feedback")),
				(button, value) -> draftConfig.toggleFeedback = value
		));
		feedback.setTooltip(Tooltip.create(Component.translatable("option.fpstune.toggle_feedback.tooltip")));
		tab.add(helpText(layout, 2, "tab.fpstune.display.help"));
		tab.add(resetButton(layout, 4, () -> {
			FPSTuneConfig defaults = new FPSTuneConfig();
			draftConfig.diagnosticsHudEnabled = defaults.diagnosticsHudEnabled;
			draftConfig.toggleFeedback = defaults.toggleFeedback;
		}));
		return tab;
	}

	private CycleButton<Integer> cycle(
			FPSTuneSettingsLayout.Advanced layout,
			int column,
			int row,
			List<Integer> presets,
			int currentValue,
			String key,
			Function<Integer, Component> formatter,
			IntConsumer onChange
	) {
		CycleButton<Integer> button = CycleButton.<Integer>builder(formatter, currentValue)
				.withValues(withCurrentValue(presets, currentValue))
				.create(
						layout.columnX(column),
						layout.rowY(row),
						layout.columnWidth(),
						20,
						Component.translatable(key),
						(cycleButton, value) -> onChange.accept(value)
				);
		button.setTooltip(Tooltip.create(Component.translatable(key + ".tooltip")));
		return button;
	}

	private MultiLineTextWidget helpText(FPSTuneSettingsLayout.Advanced layout, int row, String key) {
		MultiLineTextWidget text = new MultiLineTextWidget(Component.translatable(key), font)
				.setMaxWidth(layout.width());
		text.setX(layout.left());
		text.setY(layout.rowY(row) + 4);
		return text;
	}

	private Button resetButton(FPSTuneSettingsLayout.Advanced layout, int row, Runnable reset) {
		return Button.builder(
				Component.translatable("button.fpstune.reset_tab"),
				button -> {
					reset.run();
					rebuildWidgets();
				}
		).bounds(layout.right(), layout.rowY(row), layout.columnWidth(), 20)
				.tooltip(Tooltip.create(Component.translatable("button.fpstune.reset_tab.tooltip")))
				.build();
	}

	private void updateWidgetStates() {
		boolean admissionEnabled = draftConfig.particleAdmissionEnabled;
		boolean adaptiveEnabled = admissionEnabled && draftConfig.adaptiveParticleBudgetEnabled;
		maxParticlesWidget.active = admissionEnabled;
		adaptiveBudgetWidget.active = admissionEnabled;
		if (showMore) {
			boolean nearbyEnabled = admissionEnabled && draftConfig.prioritizeNearbyParticles;
			nearbyPriorityWidget.active = admissionEnabled;
			nearbyReserveWidget.active = nearbyEnabled;
			nearbyRangeWidget.active = nearbyEnabled;
			targetFpsWidget.active = adaptiveEnabled;
			minimumLimitWidget.active = adaptiveEnabled;
			maximumLimitWidget.active = adaptiveEnabled;
		}
	}

	static boolean hasCustomizedFineTuning(FPSTuneConfig config) {
		// Profiles own these values, so only hand-made changes count.
		if (FPSTuneConfigScreen.profileFor(config) != FPSTuneConfigScreen.PerformanceProfile.CUSTOM) {
			return false;
		}
		FPSTuneConfig defaults = new FPSTuneConfig();
		return config.prioritizeNearbyParticles != defaults.prioritizeNearbyParticles
				|| config.nearbyParticleReserve != defaults.nearbyParticleReserve
				|| config.nearbyParticleDistance != defaults.nearbyParticleDistance
				|| config.adaptiveTargetAuto != defaults.adaptiveTargetAuto
				|| (!config.adaptiveTargetAuto && config.adaptiveTargetFps != defaults.adaptiveTargetFps)
				|| config.adaptiveMinParticlesPerTick != defaults.adaptiveMinParticlesPerTick
				|| config.adaptiveMaxParticlesPerTick != defaults.adaptiveMaxParticlesPerTick;
	}

	private void returnToMain() {
		minecraft.setScreenAndShow(parent);
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

	/**
	 * A tab whose widgets are placed by {@link FPSTuneSettingsLayout}. The grid only
	 * records membership so the tab manager can add and remove the widgets.
	 */
	private static final class SettingsTab extends GridLayoutTab {
		private int children;

		SettingsTab(Component title) {
			super(title);
		}

		<T extends AbstractWidget> T add(T widget) {
			layout.addChild(widget, children++, 0);
			return widget;
		}

		@Override
		public void doLayout(ScreenRectangle area) {
			// Positions are fixed when the widgets are created on each init.
		}
	}
}

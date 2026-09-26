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

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class FPSTuneConfigScreen extends Screen {
	private static final List<PerformanceProfile> PROFILE_OPTIONS = List.of(
			PerformanceProfile.BALANCED,
			PerformanceProfile.SMOOTHER_FRAMES,
			PerformanceProfile.MAXIMUM_FPS,
			PerformanceProfile.MORE_PARTICLES,
			PerformanceProfile.CUSTOM
	);

	private static final List<FPSTuneConfig.WeatherMode> WEATHER_OPTIONS = List.of(
			FPSTuneConfig.WeatherMode.VANILLA,
			FPSTuneConfig.WeatherMode.REDUCED,
			FPSTuneConfig.WeatherMode.OFF
	);

	private final Screen parent;
	private final FPSTuneConfig draftConfig;
	private MultiLineTextWidget description;
	private CycleButton<PerformanceProfile> profileButton;
	private MultiLineTextWidget profileHelp;

	public FPSTuneConfigScreen(Screen parent) {
		super(Component.translatable("screen.fpstune.title"));
		this.parent = parent;
		FPSTuneConfig currentConfig = FPSTuneClient.config();
		this.draftConfig = currentConfig == null ? new FPSTuneConfig() : currentConfig.copy();
		this.draftConfig.clamp();
	}

	@Override
	protected void init() {
		FPSTuneSettingsLayout.Main layout = FPSTuneSettingsLayout.main(width, height);
		int left = layout.left();
		int contentWidth = layout.width();

		addRenderableOnly(new StringWidget(
				(width - font.width(getTitle())) / 2,
				6,
				font.width(getTitle()),
				12,
				getTitle(),
				font
		));
		description = addRenderableOnly(new MultiLineTextWidget(
				left,
				20,
				descriptionText(),
				font
		).setMaxWidth(contentWidth).setCentered(true));

		Checkbox enabled = addRenderableWidget(Checkbox.builder(
				FPSTuneSettingsIcons.power(Component.translatable("option.fpstune.enabled")),
				font
		).pos(left, layout.enabledY()).maxWidth(contentWidth).selected(draftConfig.enabled).onValueChange(
				(checkbox, value) -> {
					draftConfig.enabled = value;
					description.setMessage(descriptionText());
				}
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.enabled.tooltip"))).build());

		PerformanceProfile currentProfile = profileFor(draftConfig);
		profileButton = addRenderableWidget(CycleButton.<PerformanceProfile>builder(
				FPSTuneConfigScreen::formatProfile,
				currentProfile
		).withValues(cycleOptions(currentProfile)).create(
				left,
				layout.profileY(),
				contentWidth,
				20,
				FPSTuneSettingsIcons.profile(Component.translatable("option.fpstune.profile")),
				(button, value) -> {
					applyProfile(draftConfig, value);
					// A profile can change the quick switches too, so redraw them from the draft.
					rebuildWidgets();
					setFocused(profileButton);
				}
		));
		profileHelp = addRenderableOnly(new MultiLineTextWidget(
				left,
				layout.profileHelpY(),
				Component.translatable(currentProfile.helpKey()),
				font
		).setMaxWidth(contentWidth).setCentered(true));

		addRenderableWidget(Checkbox.builder(
				FPSTuneSettingsIcons.particles(Component.translatable("option.fpstune.particle_admission")),
				font
		).pos(left, layout.particlesY()).maxWidth(contentWidth).selected(draftConfig.particleAdmissionEnabled).onValueChange(
				(checkbox, value) -> {
					draftConfig.particleAdmissionEnabled = value;
					refreshProfile();
				}
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.particle_admission.tooltip"))).build());

		addRenderableWidget(weatherButton(draftConfig, left, layout.weatherY(), contentWidth));

		addRenderableWidget(Checkbox.builder(
				FPSTuneSettingsIcons.overlay(Component.translatable("option.fpstune.diagnostics_hud")),
				font
		).pos(left, layout.overlayY()).maxWidth(contentWidth).selected(draftConfig.diagnosticsHudEnabled).onValueChange(
				(checkbox, value) -> draftConfig.diagnosticsHudEnabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.diagnostics_hud.tooltip"))).build());

		addRenderableWidget(Button.builder(
				advancedButtonLabel(),
				button -> openAdvanced()
		).bounds(left, layout.advancedY(), contentWidth, 20)
				.tooltip(Tooltip.create(Component.translatable("button.fpstune.advanced.tooltip")))
				.build());

		addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> closeWithoutSaving())
				.bounds(layout.cancelX(), layout.buttonY(), layout.buttonWidth(), 20)
				.tooltip(Tooltip.create(Component.translatable("button.fpstune.cancel.tooltip")))
				.build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> saveAndClose())
				.bounds(layout.doneX(), layout.buttonY(), layout.buttonWidth(), 20)
				.build());

		setInitialFocus(enabled);
	}

	/** Rain and snow choice shared by the main screen and the Weather tab. */
	static CycleButton<FPSTuneConfig.WeatherMode> weatherButton(FPSTuneConfig config, int x, int y, int width) {
		List<FPSTuneConfig.WeatherMode> values = WEATHER_OPTIONS.contains(config.weatherMode)
				? WEATHER_OPTIONS
				: List.of(FPSTuneConfig.WeatherMode.values());
		CycleButton<FPSTuneConfig.WeatherMode> button = CycleButton.<FPSTuneConfig.WeatherMode>builder(
				mode -> Component.translatable(weatherKey(mode)),
				config.weatherMode
		).withValues(values).create(
				x,
				y,
				width,
				20,
				FPSTuneSettingsIcons.precipitation(Component.translatable("option.fpstune.weather")),
				(cycleButton, value) -> {
					config.weatherMode = value;
					cycleButton.setTooltip(weatherTooltip(value));
				}
		);
		button.setTooltip(weatherTooltip(config.weatherMode));
		return button;
	}

	private static String weatherKey(FPSTuneConfig.WeatherMode mode) {
		return "option.fpstune.weather." + mode.name().toLowerCase(Locale.ROOT);
	}

	private static Tooltip weatherTooltip(FPSTuneConfig.WeatherMode mode) {
		return Tooltip.create(Component.translatable(weatherKey(mode) + ".tooltip"));
	}

	private Component descriptionText() {
		return Component.translatable(draftConfig.enabled
				? "screen.fpstune.description"
				: "screen.fpstune.description.off");
	}

	private void refreshProfile() {
		PerformanceProfile profile = profileFor(draftConfig);
		if ((profile == PerformanceProfile.CUSTOM) != (profileButton.getValue() == PerformanceProfile.CUSTOM)) {
			// Custom joins or leaves the button's list, so rebuild it.
			rebuildWidgets();
			return;
		}
		profileButton.setValue(profile);
		profileHelp.setMessage(Component.translatable(profile.helpKey()));
	}

	/** Exposed so client smoke tests can find the button by its visible text. */
	public static Component advancedButtonLabel() {
		return FPSTuneSettingsIcons.advanced(Component.translatable("button.fpstune.advanced"));
	}

	private void openAdvanced() {
		minecraft.setScreenAndShow(new FPSTuneAdvancedConfigScreen(this, draftConfig));
	}

	static void applyProfile(FPSTuneConfig config, PerformanceProfile profile) {
		if (profile == PerformanceProfile.CUSTOM) {
			return;
		}

		profile.applyTo(config);
		config.clamp();
	}

	/**
	 * Custom is only listed while the settings really are custom. Choosing it keeps
	 * the draft unchanged, so offering it otherwise would snap back to the matching
	 * profile and stop the button from cycling.
	 */
	static List<PerformanceProfile> cycleOptions(PerformanceProfile current) {
		return current == PerformanceProfile.CUSTOM
				? PROFILE_OPTIONS
				: PROFILE_OPTIONS.stream().filter(profile -> profile != PerformanceProfile.CUSTOM).toList();
	}

		static PerformanceProfile profileFor(FPSTuneConfig config) {
		for (PerformanceProfile profile : PROFILE_OPTIONS) {
			if (profile != PerformanceProfile.CUSTOM && profile.matches(config)) {
				return profile;
			}
		}
		return PerformanceProfile.CUSTOM;
	}

	private static Component formatProfile(PerformanceProfile profile) {
		return Component.translatable("option.fpstune.profile.value", Component.translatable(profile.translationKey));
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

	enum PerformanceProfile {
		BALANCED("option.fpstune.profile.balanced", config -> {
		}),
		SMOOTHER_FRAMES("option.fpstune.profile.smoother_frames", config -> {
			config.maxParticlesPerTick = 150;
			config.adaptiveParticleBudgetEnabled = true;
			config.adaptiveMaxParticlesPerTick = 300;
		}),
		MAXIMUM_FPS("option.fpstune.profile.maximum_fps", config -> {
			config.maxParticlesPerTick = 100;
			config.nearbyParticleReserve = 50;
			config.activeParticleCapEnabled = true;
			config.maxActiveParticles = 2_000;
			config.distantParticleLimitEnabled = true;
			config.particleMaxDistance = 32;
		}),
		MORE_PARTICLES("option.fpstune.profile.more_particles", config -> config.maxParticlesPerTick = 600),
		CUSTOM("option.fpstune.profile.custom", null);

		private final String translationKey;
		// The particle settings this profile owns, starting from the defaults. Null for Custom.
		private final FPSTuneConfig values;

		PerformanceProfile(String translationKey, Consumer<FPSTuneConfig> settings) {
			this.translationKey = translationKey;
			if (settings == null) {
				this.values = null;
			} else {
				this.values = new FPSTuneConfig();
				settings.accept(this.values);
			}
		}

		String helpKey() {
			return translationKey + ".help";
		}

		private void applyTo(FPSTuneConfig config) {
			config.particleAdmissionEnabled = values.particleAdmissionEnabled;
			config.maxParticlesPerTick = values.maxParticlesPerTick;
			config.prioritizeNearbyParticles = values.prioritizeNearbyParticles;
			config.nearbyParticleReserve = values.nearbyParticleReserve;
			config.nearbyParticleDistance = values.nearbyParticleDistance;
			config.adaptiveParticleBudgetEnabled = values.adaptiveParticleBudgetEnabled;
			config.adaptiveTargetAuto = values.adaptiveTargetAuto;
			config.adaptiveTargetFps = values.adaptiveTargetFps;
			config.adaptiveMinParticlesPerTick = values.adaptiveMinParticlesPerTick;
			config.adaptiveMaxParticlesPerTick = values.adaptiveMaxParticlesPerTick;
			config.activeParticleCapEnabled = values.activeParticleCapEnabled;
			config.maxActiveParticles = values.maxActiveParticles;
			config.distantParticleLimitEnabled = values.distantParticleLimitEnabled;
			config.particleMaxDistance = values.particleMaxDistance;
		}

		private boolean matches(FPSTuneConfig config) {
			return values != null
					&& config.particleAdmissionEnabled == values.particleAdmissionEnabled
					&& config.maxParticlesPerTick == values.maxParticlesPerTick
					&& config.prioritizeNearbyParticles == values.prioritizeNearbyParticles
					&& config.nearbyParticleReserve == values.nearbyParticleReserve
					&& config.nearbyParticleDistance == values.nearbyParticleDistance
					&& config.adaptiveParticleBudgetEnabled == values.adaptiveParticleBudgetEnabled
					&& (!values.adaptiveParticleBudgetEnabled || config.adaptiveTargetAuto == values.adaptiveTargetAuto)
					&& (!values.adaptiveParticleBudgetEnabled || config.adaptiveTargetFps == values.adaptiveTargetFps)
					&& config.adaptiveMinParticlesPerTick == values.adaptiveMinParticlesPerTick
					&& config.adaptiveMaxParticlesPerTick == values.adaptiveMaxParticlesPerTick
					&& config.activeParticleCapEnabled == values.activeParticleCapEnabled
					&& (!values.activeParticleCapEnabled || config.maxActiveParticles == values.maxActiveParticles)
					&& config.distantParticleLimitEnabled == values.distantParticleLimitEnabled
					&& (!values.distantParticleLimitEnabled || config.particleMaxDistance == values.particleMaxDistance);
		}
	}
}

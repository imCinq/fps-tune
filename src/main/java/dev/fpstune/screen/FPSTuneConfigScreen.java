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

public final class FPSTuneConfigScreen extends Screen {
	private static final List<PerformanceProfile> PROFILE_OPTIONS = List.of(
			PerformanceProfile.BALANCED,
			PerformanceProfile.SMOOTHER_FRAMES,
			PerformanceProfile.MORE_PARTICLES,
			PerformanceProfile.CUSTOM
	);

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
		FPSTuneConfigLayout.BasicLayout layout = FPSTuneConfigLayout.calculateBasic(width, height);
		int contentWidth = layout.contentWidth();
		int left = layout.left();

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
				Component.translatable("screen.fpstune.description"),
				font
		).setMaxWidth(contentWidth).setCentered(true));

		Checkbox enabled = addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.enabled"),
				font
		).pos(left, layout.enabledY()).maxWidth(contentWidth).selected(draftConfig.enabled).onValueChange(
				(checkbox, value) -> draftConfig.enabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.enabled.tooltip"))).build());

		PerformanceProfile currentProfile = profileFor(draftConfig);
		addRenderableWidget(CycleButton.<PerformanceProfile>builder(
				FPSTuneConfigScreen::formatProfile,
				currentProfile
		).withValues(PROFILE_OPTIONS).create(
				left,
				layout.profileY(),
				contentWidth,
				20,
				Component.translatable("option.fpstune.profile"),
				(button, value) -> applyProfile(draftConfig, value)
		));

		addRenderableOnly(new MultiLineTextWidget(
				left,
				layout.profileHelpY(),
				Component.translatable("option.fpstune.profile.help"),
				font
		).setMaxWidth(contentWidth).setCentered(true));

		addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.weather_rendering"),
				font
		).pos(left, layout.weatherY()).maxWidth(contentWidth).selected(draftConfig.weatherRenderingEnabled).onValueChange(
				(checkbox, value) -> draftConfig.weatherRenderingEnabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.weather_rendering.tooltip"))).build());

		addRenderableWidget(Checkbox.builder(
				Component.translatable("option.fpstune.diagnostics_hud"),
				font
		).pos(left, layout.diagnosticsY()).maxWidth(contentWidth).selected(draftConfig.diagnosticsHudEnabled).onValueChange(
				(checkbox, value) -> draftConfig.diagnosticsHudEnabled = value
		).tooltip(Tooltip.create(Component.translatable("option.fpstune.diagnostics_hud.tooltip"))).build());

		addRenderableWidget(Button.builder(
				Component.translatable("button.fpstune.advanced"),
				button -> openAdvanced()
		).bounds(left, layout.advancedY(), contentWidth, 20)
				.tooltip(Tooltip.create(Component.translatable("button.fpstune.advanced.tooltip")))
				.build());

		addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> closeWithoutSaving())
				.bounds(left, layout.buttonY(), layout.actionButtonWidth(), 20)
				.build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> saveAndClose())
				.bounds(layout.doneButtonX(), layout.buttonY(), layout.actionButtonWidth(), 20)
				.build());

		setInitialFocus(enabled);
	}

	private void openAdvanced() {
		minecraft.setScreenAndShow(new FPSTuneAdvancedConfigScreen(this, draftConfig));
	}

	static void applyProfile(FPSTuneConfig config, PerformanceProfile profile) {
		if (profile == PerformanceProfile.CUSTOM) {
			return;
		}

		config.particleAdmissionEnabled = profile.particleAdmissionEnabled;
		config.maxParticlesPerTick = profile.maxParticlesPerTick;
		config.prioritizeNearbyParticles = profile.prioritizeNearbyParticles;
		config.nearbyParticleReserve = profile.nearbyParticleReserve;
		config.nearbyParticleDistance = profile.nearbyParticleDistance;
		config.adaptiveParticleBudgetEnabled = profile.adaptiveParticleBudgetEnabled;
		config.adaptiveTargetAuto = profile.adaptiveTargetAuto;
		config.adaptiveTargetFps = profile.adaptiveTargetFps;
		config.adaptiveMinParticlesPerTick = profile.adaptiveMinParticlesPerTick;
		config.adaptiveMaxParticlesPerTick = profile.adaptiveMaxParticlesPerTick;
		config.clamp();
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
		BALANCED(
				"option.fpstune.profile.balanced",
				true,
				300,
				true,
				100,
				16,
				false,
				true,
				120,
				100,
				2_000
		),
		SMOOTHER_FRAMES(
				"option.fpstune.profile.smoother_frames",
				true,
				150,
				true,
				100,
				16,
				true,
				true,
				120,
				100,
				300
		),
		MORE_PARTICLES(
				"option.fpstune.profile.more_particles",
				true,
				600,
				true,
				100,
				16,
				false,
				true,
				120,
				100,
				2_000
		),
		CUSTOM(
				"option.fpstune.profile.custom",
				false,
				0,
				false,
				0,
				0,
				false,
				true,
				120,
				0,
				0
		);

		private final String translationKey;
		private final boolean particleAdmissionEnabled;
		private final int maxParticlesPerTick;
		private final boolean prioritizeNearbyParticles;
		private final int nearbyParticleReserve;
		private final int nearbyParticleDistance;
		private final boolean adaptiveParticleBudgetEnabled;
		private final boolean adaptiveTargetAuto;
		private final int adaptiveTargetFps;
		private final int adaptiveMinParticlesPerTick;
		private final int adaptiveMaxParticlesPerTick;

		PerformanceProfile(
				String translationKey,
				boolean particleAdmissionEnabled,
				int maxParticlesPerTick,
				boolean prioritizeNearbyParticles,
				int nearbyParticleReserve,
				int nearbyParticleDistance,
				boolean adaptiveParticleBudgetEnabled,
				boolean adaptiveTargetAuto,
				int adaptiveTargetFps,
				int adaptiveMinParticlesPerTick,
				int adaptiveMaxParticlesPerTick
		) {
			this.translationKey = translationKey;
			this.particleAdmissionEnabled = particleAdmissionEnabled;
			this.maxParticlesPerTick = maxParticlesPerTick;
			this.prioritizeNearbyParticles = prioritizeNearbyParticles;
			this.nearbyParticleReserve = nearbyParticleReserve;
			this.nearbyParticleDistance = nearbyParticleDistance;
			this.adaptiveParticleBudgetEnabled = adaptiveParticleBudgetEnabled;
			this.adaptiveTargetAuto = adaptiveTargetAuto;
			this.adaptiveTargetFps = adaptiveTargetFps;
			this.adaptiveMinParticlesPerTick = adaptiveMinParticlesPerTick;
			this.adaptiveMaxParticlesPerTick = adaptiveMaxParticlesPerTick;
		}

		private boolean matches(FPSTuneConfig config) {
			return config.particleAdmissionEnabled == particleAdmissionEnabled
					&& config.maxParticlesPerTick == maxParticlesPerTick
					&& config.prioritizeNearbyParticles == prioritizeNearbyParticles
					&& config.nearbyParticleReserve == nearbyParticleReserve
					&& config.nearbyParticleDistance == nearbyParticleDistance
					&& config.adaptiveParticleBudgetEnabled == adaptiveParticleBudgetEnabled
					&& (!adaptiveParticleBudgetEnabled || config.adaptiveTargetAuto == adaptiveTargetAuto)
					&& (!adaptiveParticleBudgetEnabled || config.adaptiveTargetFps == adaptiveTargetFps)
					&& config.adaptiveMinParticlesPerTick == adaptiveMinParticlesPerTick
					&& config.adaptiveMaxParticlesPerTick == adaptiveMaxParticlesPerTick;
		}
	}
}

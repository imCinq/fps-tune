package dev.fpstune.screen;

import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;

/**
 * Builds vanilla's tab bar. From 26.2 the caller supplies the tab buttons and
 * bar bounds; the widths match the older automatic layout.
 */
final class FPSTuneTabBars {
	private static final int TAB_HEIGHT = 24;
	private static final int MAXIMUM_BAR_WIDTH = 400;
	private static final int BAR_MARGIN = 28;

	private FPSTuneTabBars() {
	}

	static TabNavigationBar create(TabManager tabManager, int screenWidth, Tab... tabs) {
		int tabWidth = Math.max(1, (Math.min(MAXIMUM_BAR_WIDTH, screenWidth) - BAR_MARGIN) / tabs.length);
		TabNavigationBar.Builder builder = TabNavigationBar.builder(tabManager, 0, 0, screenWidth, TAB_HEIGHT);
		for (Tab tab : tabs) {
			builder.addTab(new TabButton(tabManager, tab, tabWidth, TAB_HEIGHT), tab);
		}
		TabNavigationBar tabBar = builder.build();
		tabBar.arrangeElements(screenWidth);
		return tabBar;
	}
}

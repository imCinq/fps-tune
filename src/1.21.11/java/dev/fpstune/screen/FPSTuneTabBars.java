package dev.fpstune.screen;

import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;

/**
 * Builds vanilla's tab bar. 1.21.11 sizes and places the tab buttons itself.
 */
final class FPSTuneTabBars {
	private FPSTuneTabBars() {
	}

	static TabNavigationBar create(TabManager tabManager, int screenWidth, Tab... tabs) {
		TabNavigationBar tabBar = TabNavigationBar.builder(tabManager, screenWidth).addTabs(tabs).build();
		tabBar.arrangeElements();
		return tabBar;
	}
}

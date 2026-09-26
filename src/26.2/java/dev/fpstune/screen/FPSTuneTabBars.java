package dev.fpstune.screen;

import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;

/**
 * Builds vanilla's menu tab bar, the one 26.x uses for its own settings screens.
 */
final class FPSTuneTabBars {
	private FPSTuneTabBars() {
	}

	static TabNavigationBar create(TabManager tabManager, int screenWidth, Tab... tabs) {
		TabNavigationBar tabBar = MenuTabBar.builder(tabManager, screenWidth).addTabs(tabs).build();
		tabBar.arrangeElements(screenWidth);
		return tabBar;
	}
}

package dev.fpstune.screen;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneSettingsLayoutTest {
	// GUI scale 4 at 1280x960 and 1920x1080, a small 854x480 window at scale 2, and a roomy screen.
	private static final int[][] SCREEN_SIZES = {
			{FPSTuneSettingsLayout.MINIMUM_GUI_WIDTH, FPSTuneSettingsLayout.MINIMUM_GUI_HEIGHT},
			{480, 270},
			{427, 240},
			{854, 480}
	};

	@Test
	void mainScreenFitsWithoutOverlapAtSmallSizes() {
		for (int[] size : SCREEN_SIZES) {
			assertFitsWithoutOverlap(FPSTuneSettingsLayout.main(size[0], size[1]).interactiveBounds(), size);
		}
	}

	@Test
	void advancedScreenFitsWithoutOverlapAtSmallSizes() {
		for (int[] size : SCREEN_SIZES) {
			FPSTuneSettingsLayout.Advanced layout = FPSTuneSettingsLayout.advanced(size[0], size[1]);
			assertFitsWithoutOverlap(layout.interactiveBounds(), size);
			assertTrue(layout.rowY(0) >= FPSTuneSettingsLayout.TAB_BAR_HEIGHT, "rows start below the tab bar");
		}
	}

	@Test
	void mainScreenKeepsProfileHelpBetweenProfileAndQuickSwitches() {
		FPSTuneSettingsLayout.Main layout = FPSTuneSettingsLayout.main(427, 240);
		assertTrue(layout.profileHelpY() >= layout.profileY() + FPSTuneSettingsLayout.WIDGET_HEIGHT);
		assertTrue(layout.profileHelpY() + 10 <= layout.particlesY());
	}

	private static void assertFitsWithoutOverlap(List<FPSTuneSettingsLayout.Bounds> bounds, int[] size) {
		for (int i = 0; i < bounds.size(); i++) {
			assertTrue(bounds.get(i).within(size[0], size[1]), bounds.get(i) + " outside " + size[0] + "x" + size[1]);
			for (int j = i + 1; j < bounds.size(); j++) {
				assertFalse(bounds.get(i).overlaps(bounds.get(j)), bounds.get(i) + " overlaps " + bounds.get(j));
			}
		}
	}
}

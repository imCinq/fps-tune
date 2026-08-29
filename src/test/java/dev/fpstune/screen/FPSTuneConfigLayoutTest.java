package dev.fpstune.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FPSTuneConfigLayoutTest {
	@Test
	void basicScreenFitsAtTheMinimumAutomaticGuiHeight() {
		FPSTuneConfigLayout.BasicLayout layout = FPSTuneConfigLayout.calculateBasic(
				427,
				FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT
		);

		assertEquals(48, layout.top());
		assertEquals(220, layout.buttonY());
		assertTrue(layout.allWidgetsWithin(427, FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT));
	}

	@Test
	void advancedScreenFitsAtTheMinimumAutomaticGuiHeight() {
		FPSTuneConfigLayout.AdvancedLayout layout = FPSTuneConfigLayout.calculateAdvanced(
				427,
				FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT
		);

		assertEquals(44, layout.top());
		assertEquals(180, layout.resetY());
		assertEquals(220, layout.buttonY());
		assertTrue(layout.allWidgetsWithin(427, FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT));
	}

	@Test
	void bothScreensFitSupportedWidthsAtMinimumHeight() {
		for (int width : new int[]{320, 427, 640, 854}) {
			FPSTuneConfigLayout.BasicLayout basic = FPSTuneConfigLayout.calculateBasic(
					width,
					FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT
			);
			FPSTuneConfigLayout.AdvancedLayout advanced = FPSTuneConfigLayout.calculateAdvanced(
					width,
					FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT
			);

			assertTrue(
					basic.allWidgetsWithin(width, FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT),
					() -> "basic layout exceeds screen at " + width + "x" + FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT
			);
			assertTrue(
					advanced.allWidgetsWithin(width, FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT),
					() -> "advanced layout exceeds screen at " + width + "x" + FPSTuneConfigLayout.MINIMUM_GUI_HEIGHT
			);
		}
	}

	@Test
	void largerScreensKeepBothLayoutsInsideTheScreen() {
		for (int[] size : new int[][]{{427, 240}, {855, 480}, {1280, 720}}) {
			FPSTuneConfigLayout.BasicLayout basic = FPSTuneConfigLayout.calculateBasic(size[0], size[1]);
			FPSTuneConfigLayout.AdvancedLayout advanced = FPSTuneConfigLayout.calculateAdvanced(size[0], size[1]);

			assertTrue(basic.allWidgetsWithin(size[0], size[1]));
			assertTrue(advanced.allWidgetsWithin(size[0], size[1]));
		}
	}
}

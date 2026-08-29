package dev.fpstune.screen;

import java.util.List;

/**
 * The compact, scroll-free layouts used by the FPS Tune settings screens.
 *
 * <p>Minecraft's automatic GUI scale can leave a client with only 240 logical
 * pixels of vertical space, even on a high-resolution display. Keeping the
 * geometry separate makes that minimum easy to test without loading Minecraft's
 * client classes.</p>
 */
final class FPSTuneConfigLayout {
	static final int CONTENT_WIDTH = 420;
	static final int BUTTON_WIDTH = 120;
	static final int RESET_BUTTON_WIDTH = 160;
	static final int WIDGET_HEIGHT = 20;
	static final int MINIMUM_GUI_HEIGHT = 240;

	private static final int DESCRIPTION_Y = 20;
	private static final int BASIC_INITIAL_TOP = 48;
	private static final int BASIC_MINIMUM_LAYOUT_HEIGHT = 178;
	private static final int BASIC_CONTROL_STEP = 25;
	private static final int BASIC_PROFILE_HELP_OFFSET = 49;
	private static final int BASIC_ADVANCED_OFFSET = 132;
	private static final int ADVANCED_INITIAL_TOP = 44;
	private static final int ADVANCED_MINIMUM_LAYOUT_HEIGHT = 176;
	private static final int ADVANCED_HEADING_HEIGHT = 12;
	private static final int ADVANCED_CONTROL_OFFSET = 16;
	private static final int ADVANCED_CONTROL_STEP = 24;
	private static final int ADVANCED_RESET_OFFSET = 136;
	private static final int COLUMN_GAP = 12;

	private FPSTuneConfigLayout() {
	}

	static int descriptionY() {
		return DESCRIPTION_Y;
	}

	static BasicLayout calculateBasic(int screenWidth, int screenHeight) {
		int contentWidth = contentWidth(screenWidth);
		int left = (screenWidth - contentWidth) / 2;
		int top = Math.min(
				BASIC_INITIAL_TOP,
				Math.max(0, screenHeight - BASIC_MINIMUM_LAYOUT_HEIGHT)
		);
		int buttonY = Math.max(0, screenHeight - WIDGET_HEIGHT);
		int actionButtonWidth = actionButtonWidth(contentWidth);

		return new BasicLayout(
				contentWidth,
				left,
				top,
				top,
				top + BASIC_CONTROL_STEP,
				top + BASIC_PROFILE_HELP_OFFSET,
				top + BASIC_CONTROL_STEP * 3,
				top + BASIC_CONTROL_STEP * 4,
				top + BASIC_ADVANCED_OFFSET,
				buttonY,
				actionButtonWidth,
				screenWidth - left - actionButtonWidth
		);
	}

	static AdvancedLayout calculateAdvanced(int screenWidth, int screenHeight) {
		int contentWidth = contentWidth(screenWidth);
		int left = (screenWidth - contentWidth) / 2;
		int columnWidth = Math.max(1, (contentWidth - COLUMN_GAP) / 2);
		int right = left + columnWidth + COLUMN_GAP;
		int top = Math.min(
				ADVANCED_INITIAL_TOP,
				Math.max(0, screenHeight - ADVANCED_MINIMUM_LAYOUT_HEIGHT)
		);
		int controlTop = top + ADVANCED_CONTROL_OFFSET;
		int buttonY = Math.max(0, screenHeight - WIDGET_HEIGHT);

		return new AdvancedLayout(
				contentWidth,
				left,
				right,
				columnWidth,
				top,
				controlTop,
				controlTop + ADVANCED_CONTROL_STEP,
				controlTop + ADVANCED_CONTROL_STEP * 2,
				controlTop + ADVANCED_CONTROL_STEP * 3,
				controlTop + ADVANCED_CONTROL_STEP * 4,
				buttonY,
				top + ADVANCED_RESET_OFFSET,
				Math.min(RESET_BUTTON_WIDTH, contentWidth)
		);
	}

	private static int contentWidth(int screenWidth) {
		return Math.min(CONTENT_WIDTH, Math.max(200, screenWidth - 40));
	}

	private static int actionButtonWidth(int contentWidth) {
		return Math.min(BUTTON_WIDTH, Math.max(1, (contentWidth - COLUMN_GAP) / 2));
	}

	record BasicLayout(
			int contentWidth,
			int left,
			int top,
			int enabledY,
			int profileY,
			int profileHelpY,
			int weatherY,
			int diagnosticsY,
			int advancedY,
			int buttonY,
			int actionButtonWidth,
			int doneButtonX
	) {
		List<Bounds> interactiveBounds() {
			return List.of(
					bounds(left, enabledY, contentWidth, WIDGET_HEIGHT),
					bounds(left, profileY, contentWidth, WIDGET_HEIGHT),
					bounds(left, weatherY, contentWidth, WIDGET_HEIGHT),
					bounds(left, diagnosticsY, contentWidth, WIDGET_HEIGHT),
					bounds(left, advancedY, contentWidth, WIDGET_HEIGHT),
					bounds(left, buttonY, actionButtonWidth, WIDGET_HEIGHT),
					bounds(doneButtonX, buttonY, actionButtonWidth, WIDGET_HEIGHT)
			);
		}

		boolean allWidgetsWithin(int screenWidth, int screenHeight) {
			return interactiveBounds().stream().allMatch(bound -> bound.within(screenWidth, screenHeight));
		}
	}

	record AdvancedLayout(
			int contentWidth,
			int left,
			int right,
			int columnWidth,
			int top,
			int firstControlY,
			int secondControlY,
			int thirdControlY,
			int fourthControlY,
			int fifthControlY,
			int buttonY,
			int resetY,
			int resetButtonWidth
	) {
		int headingHeight() {
			return ADVANCED_HEADING_HEIGHT;
		}

		List<Bounds> interactiveBounds() {
			return List.of(
					bounds(left, firstControlY, columnWidth, WIDGET_HEIGHT),
					bounds(left, secondControlY, columnWidth, WIDGET_HEIGHT),
					bounds(left, thirdControlY, columnWidth, WIDGET_HEIGHT),
					bounds(left, fourthControlY, columnWidth, WIDGET_HEIGHT),
					bounds(left, fifthControlY, columnWidth, WIDGET_HEIGHT),
					bounds(right, firstControlY, columnWidth, WIDGET_HEIGHT),
					bounds(right, secondControlY, columnWidth, WIDGET_HEIGHT),
					bounds(right, thirdControlY, columnWidth, WIDGET_HEIGHT),
					bounds(right, fourthControlY, columnWidth, WIDGET_HEIGHT),
					bounds(left, resetY, resetButtonWidth, WIDGET_HEIGHT),
					bounds(left, buttonY, BUTTON_WIDTH, WIDGET_HEIGHT)
			);
		}

		boolean allWidgetsWithin(int screenWidth, int screenHeight) {
			return interactiveBounds().stream().allMatch(bound -> bound.within(screenWidth, screenHeight));
		}
	}

	record Bounds(int x, int y, int width, int height) {
		boolean within(int screenWidth, int screenHeight) {
			return x >= 0 && y >= 0 && width > 0 && height > 0
					&& x + width <= screenWidth
					&& y + height <= screenHeight;
		}
	}

	private static Bounds bounds(int x, int y, int width, int height) {
		return new Bounds(x, y, width, height);
	}
}

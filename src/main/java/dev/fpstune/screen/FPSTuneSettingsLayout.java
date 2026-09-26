package dev.fpstune.screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Scroll-free geometry for the settings screens.
 *
 * <p>Minecraft's automatic GUI scale can leave only 240 logical pixels of height
 * (for example GUI scale 4 on a 1080p display). Keeping the geometry free of
 * Minecraft classes lets that minimum be unit tested.</p>
 */
final class FPSTuneSettingsLayout {
	static final int WIDGET_HEIGHT = 20;
	static final int ROW_STEP = 24;
	static final int TAB_BAR_HEIGHT = 24;
	static final int MINIMUM_GUI_WIDTH = 320;
	static final int MINIMUM_GUI_HEIGHT = 240;
	static final int ADVANCED_ROWS = 6;

	private static final int MAIN_WIDTH = 310;
	private static final int ADVANCED_WIDTH = 420;
	private static final int COLUMN_GAP = 12;
	private static final int BUTTON_GAP = 8;
	private static final int BUTTON_WIDTH = 120;
	private static final int BOTTOM_MARGIN = 4;
	private static final int MAIN_TOP = 44;
	// Master switch through the Advanced button, a gap, then Cancel/Done.
	private static final int MAIN_HEIGHT = 196;
	private static final int ADVANCED_TOP_GAP = 8;

	private FPSTuneSettingsLayout() {
	}

	static Main main(int screenWidth, int screenHeight) {
		int width = contentWidth(screenWidth, MAIN_WIDTH);
		int left = (screenWidth - width) / 2;
		int top = Math.min(MAIN_TOP, Math.max(0, screenHeight - MAIN_HEIGHT));
		int buttonWidth = Math.min(BUTTON_WIDTH, (width - BUTTON_GAP) / 2);
		int center = screenWidth / 2;
		return new Main(
				left,
				width,
				top,
				top + 28,
				top + 50,
				top + 66,
				top + 90,
				top + 114,
				top + 140,
				bottomButtonY(screenHeight),
				buttonWidth,
				center - BUTTON_GAP / 2 - buttonWidth,
				center + BUTTON_GAP / 2
		);
	}

	static Advanced advanced(int screenWidth, int screenHeight) {
		int width = contentWidth(screenWidth, ADVANCED_WIDTH);
		int left = (screenWidth - width) / 2;
		int columnWidth = (width - COLUMN_GAP) / 2;
		int backY = bottomButtonY(screenHeight);
		int lastRowSpace = (ADVANCED_ROWS - 1) * ROW_STEP + WIDGET_HEIGHT + BOTTOM_MARGIN;
		int firstRowY = Math.min(
				TAB_BAR_HEIGHT + ADVANCED_TOP_GAP,
				Math.max(TAB_BAR_HEIGHT, backY - lastRowSpace)
		);
		int backWidth = Math.min(BUTTON_WIDTH, width);
		return new Advanced(
				left,
				left + columnWidth + COLUMN_GAP,
				columnWidth,
				width,
				firstRowY,
				(screenWidth - backWidth) / 2,
				backY,
				backWidth
		);
	}

	private static int contentWidth(int screenWidth, int preferredWidth) {
		return Math.min(preferredWidth, Math.max(200, screenWidth - 20));
	}

	private static int bottomButtonY(int screenHeight) {
		return Math.max(0, screenHeight - WIDGET_HEIGHT - BOTTOM_MARGIN);
	}

	record Main(
			int left,
			int width,
			int enabledY,
			int profileY,
			int profileHelpY,
			int particlesY,
			int weatherY,
			int overlayY,
			int advancedY,
			int buttonY,
			int buttonWidth,
			int cancelX,
			int doneX
	) {
		List<Bounds> interactiveBounds() {
			return List.of(
					new Bounds(left, enabledY, width, WIDGET_HEIGHT),
					new Bounds(left, profileY, width, WIDGET_HEIGHT),
					new Bounds(left, particlesY, width, WIDGET_HEIGHT),
					new Bounds(left, weatherY, width, WIDGET_HEIGHT),
					new Bounds(left, overlayY, width, WIDGET_HEIGHT),
					new Bounds(left, advancedY, width, WIDGET_HEIGHT),
					new Bounds(cancelX, buttonY, buttonWidth, WIDGET_HEIGHT),
					new Bounds(doneX, buttonY, buttonWidth, WIDGET_HEIGHT)
			);
		}
	}

	record Advanced(
			int left,
			int right,
			int columnWidth,
			int width,
			int firstRowY,
			int backX,
			int backY,
			int backWidth
	) {
		int rowY(int row) {
			return firstRowY + row * ROW_STEP;
		}

		int columnX(int column) {
			return column == 0 ? left : right;
		}

		List<Bounds> interactiveBounds() {
			List<Bounds> bounds = new ArrayList<>();
			for (int row = 0; row < ADVANCED_ROWS; row++) {
				bounds.add(new Bounds(left, rowY(row), columnWidth, WIDGET_HEIGHT));
				bounds.add(new Bounds(right, rowY(row), columnWidth, WIDGET_HEIGHT));
			}
			bounds.add(new Bounds(backX, backY, backWidth, WIDGET_HEIGHT));
			return bounds;
		}
	}

	record Bounds(int x, int y, int width, int height) {
		boolean within(int screenWidth, int screenHeight) {
			return x >= 0 && y >= 0 && width > 0 && height > 0
					&& x + width <= screenWidth
					&& y + height <= screenHeight;
		}

		boolean overlaps(Bounds other) {
			return x < other.x + other.width && other.x < x + width
					&& y < other.y + other.height && other.y < y + height;
		}
	}
}

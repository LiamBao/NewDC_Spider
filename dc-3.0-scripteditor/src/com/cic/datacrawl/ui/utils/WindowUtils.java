package com.cic.datacrawl.ui.utils;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.Toolkit;

public class WindowUtils {
	private static int windowWidth;
	private static int windowHeight;
	private static Robot robot;

	public static Robot getRobot() {
		if (robot == null) {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			if (gs.length > 0) {

				try {
					robot = new Robot(gs[0]);
				} catch (AWTException e) {
				}
			}
		}
		return robot;
	}

	public static void showFrameAtScreenCenter(Component component) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = component.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		component.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		component.setVisible(true);
	}

	/**
	 * @return the windowWidth
	 */
	public static int getWindowWidth() {
		if (windowWidth == 0)
			windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		return windowWidth;
	}

	/**
	 * @return the windowHeight
	 */
	public static int getWindowHeight() {
		if (windowHeight == 0)
			windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		return windowHeight;
	}

	public static Dimension DEFAULT_BUTTON_SIZE = new Dimension(20, 20);

	public static Dimension DEFAULT_MAX_WINDOW_SIZE = new Dimension(
			getWindowWidth(), getWindowHeight());
	public static Dimension DEFAULT_MIN_WINDOW_SIZE = new Dimension(120, 40);
}

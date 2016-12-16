package com.cic.datacrawl.ui.utils;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.cic.datacrawl.core.util.FileUtils;
import com.cic.datacrawl.ui.MessageDialogWrapper;

public class SwingFileUtils {

	/**
	 * Reads the file with the given name and returns its contents as a String.
	 */
	public static String readFile(String fileName, Component parentComponent) {
		String text;
		try {
			text = FileUtils.readFile(fileName, "UTF-8");
		} catch (IOException ex) {
			MessageDialogWrapper.showMessageDialog(parentComponent, ex
					.getMessage(), "Error reading " + fileName,
					JOptionPane.ERROR_MESSAGE);
			text = null;
		}
		return text;
	}

	/**
	 * Reads the file with the given name and returns its contents as a String.
	 */
	public static File saveFile(String fileName, String text,
			Component parentComponent) {
		File ret = null;
		try {
			ret = FileUtils.saveFile(fileName, text, true);
		} catch (IOException ex) {
			MessageDialogWrapper.showMessageDialog(parentComponent, ex
					.getMessage(), "Error saving " + fileName,
					JOptionPane.ERROR_MESSAGE);

		}
		return ret;
	}
}

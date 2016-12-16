package com.cic.datacrawl.core.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.util.FileUtils;

public class SpringConfiguration {
	private String outputPath;
	private String splitMaxTask;
	public String getSplitMaxTask() {
		return splitMaxTask;
	}

	public void setSplitMaxTask(String splitMaxTask) {
		this.splitMaxTask = splitMaxTask;
	}

	private int invalidDate = 7;
	private boolean showBrowser;

	/**
	 * @return the showBrowser
	 */
	public boolean isShowBrowser() {
		return showBrowser;
	}

	/**
	 * @param showBrowser the showBrowser to set
	 */
	public void setShowBrowser(boolean showBrowser) {
		this.showBrowser = showBrowser;
	}

	/**
	 * @return the invalidDate
	 */
	public int getInvalidDate() {
		return invalidDate;
	}

	/**
	 * @param invalidDate
	 *            the invalidDate to set
	 */
	public void setInvalidDate(int invalidDate) {
		this.invalidDate = invalidDate;
	}

	private Map<String, Object> customPropertiesMap = new HashMap<String, Object>();

	/**
	 * @return the outputPath
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * @param outputPath
	 *            the outputPath to set
	 */
	public void setOutputPath(String outputPath) {
		File outputFile = FileUtils.makeDirs(outputPath);
		this.outputPath = outputFile.getAbsolutePath() + File.separator;
	}
	

	public String getCustomProperty(String key) {
		if (customPropertiesMap.containsKey(key)) {
			return ObjectUtils.toString(customPropertiesMap.get(key));
		}
		return null;
	}

	public Object getCustomPropertyObject(String key) {
		if (customPropertiesMap.containsKey(key)) {
			return customPropertiesMap.get(key);
		}
		return null;
	}
	/**
	 * @param customPropertiesMap
	 *            the customPropertiesMap to set
	 */
	public void setCustomPropertiesMap(Map<String, Object> customPropertiesMap) {
		this.customPropertiesMap = customPropertiesMap;
	}

}

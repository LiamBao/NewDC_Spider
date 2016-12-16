package com.cic.datacrawl.management.config;

import com.cic.datacrawl.core.util.StringUtil;

public class LocaleControlConfig {
	public static long getCurrentRecordId() {
		long ret = Long.MIN_VALUE;

		try {
			ret = Long.parseLong(System.getProperty("Task.Record.Id"));
		} catch (Exception e) {
		}
		return ret;
	}

	

	public static void setCurrentRecordId(long recordId) {
		System.setProperty("Task.Record.Id", "" + recordId);

	}

	public static String getCurrentRecordKey() {
		String key = System.getProperty("Task.Record.Key");
		if (key == null) {
			key = StringUtil.buildRandomString(10);
			System.setProperty("Task.Record.Key", key);
		}
		return key;
	}

	public static String buildNewRecordKey() {
		String key = StringUtil.buildRandomString(10);
		System.setProperty("Task.Record.Key", key);

		return key;
	}

	public static void setTimeout(long timeout) {
		System.setProperty("Task.Record.Timeout", "" + timeout);
	}

	public static long getTimeout() {
		long ret = Long.MIN_VALUE;

		try {
			ret = Long.parseLong(System.getProperty("Task.Record.Timeout"));
		} catch (Exception e) {
		}
		return ret;
	}
}

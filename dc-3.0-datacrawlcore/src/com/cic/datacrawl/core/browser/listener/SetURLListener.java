package com.cic.datacrawl.core.browser.listener;

public interface SetURLListener {
	public void doBeforeUrlLocationChanged(String url) throws Exception;

	public void doAfterUrlLocationChanged(String url) throws Exception;
}

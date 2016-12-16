package com.cic.datacrawl.core.browser.tools;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;
import com.cic.datacrawl.ui.beans.BrowserDefination;

public class WebBrowserFactory {
	private static final Logger LOG = Logger.getLogger(WebBrowserFactory.class);
	public static final String NAME_DEFAULT_BROWSER = "NAME_DEFAULT_BROWSER";

	public static AbstractJavaWebBrowser getInstance(String webBrowserName) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		if (NAME_DEFAULT_BROWSER.equals(webBrowserName)) {
			LOG.info("Get Default Browser");
			BrowserList browserList = (BrowserList) ApplicationContext.getInstance().getBean("browserList");
			if(browserList == null){
			    LOG.error("browserList is null");
			    return null;
			}
			// LOG.info("browserList size : " + browserList.getBrowserDefinationList().size());
			return browserList.getDefalutBrowser();
		} else {
			if (LOG.isDebugEnabled())
				LOG.trace("Get Browser: " + webBrowserName);
			return ((BrowserList) ApplicationContext.getInstance().getBean("browserList"))
					.getBrowser(webBrowserName);
		}
	}

	public static AbstractJavaWebBrowser newInstance(String webBrowserName) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		BrowserDefination defination = null;
		if (NAME_DEFAULT_BROWSER.equals(webBrowserName)) {
			LOG.trace("create Default Browser");
			defination = ((BrowserList) ApplicationContext.getInstance().getBean("browserList"))
					.getDefaultDefination();
		} else {
			if (LOG.isDebugEnabled())
				LOG.trace("create Browser: " + webBrowserName);
			defination = ((BrowserList) ApplicationContext.getInstance().getBean("browserList"))
					.getDefination(webBrowserName);
		}
		if (defination == null)
			return null;

		return (AbstractJavaWebBrowser) Class.forName(defination.getClassName()).newInstance();
	}
}

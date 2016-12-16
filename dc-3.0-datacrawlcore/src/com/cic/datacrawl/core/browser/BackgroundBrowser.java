package com.cic.datacrawl.core.browser;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.browser.tools.WebBrowserFactory;

/**
 * 后台运行的SWT Browser
 */
public class BackgroundBrowser {
	private static final Logger LOG = Logger.getLogger(BackgroundBrowser.class);
	private JavaWebBrowserImpl browser;
	private String browserName = "";

	public BackgroundBrowser() {
	}

	public void setBrowser(String browserName) {
		if (browserName == null || browserName.trim().length() == 0) {
			browserName = WebBrowserFactory.NAME_DEFAULT_BROWSER;
		}
		if (!this.browserName.equals(browserName)) {
			if (browser != null)
				dispose();

			final String finalBrowserName = browserName;
			AbstractJavaWebBrowser javaBrowser = null;
			try {
				javaBrowser = WebBrowserFactory.getInstance(finalBrowserName);
			} catch (Throwable e) {
			    LOG.error(" WebBrowserFactory.getInstance error",e);
			}

			browser = JavaWebBrowserImpl.getInstance(javaBrowser);
			browser.syncExec(new Runnable() {
				public void run() {
					browser.show();
					
				}
			});
			this.browserName = browserName;
		}
	}

	public JavaWebBrowserImpl getWebBrowser() {
		if (browser == null) {
			setBrowser(WebBrowserFactory.NAME_DEFAULT_BROWSER);
		}
		return browser;
	}

	public void dispose() {
		browser.dispose();
	}
}

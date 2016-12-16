package com.cic.datacrawl.core.browser;

import java.awt.Dimension;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.browser.tools.WebBrowserFactory;

/**
 * 嵌入Swing中的SWT Browser
 */
public class SwingBrowser {
	private static final Logger LOG = Logger.getLogger(SwingBrowser.class);
	protected JavaWebBrowserImpl browser;
	private JPanel container;
	private String browserName = "";

	/**
	 * @param container
	 *            the container to set
	 */
	protected void setContainer(JPanel container) {
		this.container = container;

	}

	/**
	 * @return the container
	 */
	protected JPanel getContainer() {
		return container;
	}

	public SwingBrowser(JPanel container) {
		setContainer(container);

		// 避免容器JSplitPane无法正常拖动
		container.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				getContainer().setMinimumSize(new Dimension(100, 100));
			}
		});

		container.setMinimumSize(new Dimension(100, 100));
	}

	public void setBrowser(String browserName, final boolean createNew) {
		if (LOG.isDebugEnabled())
			LOG.debug("param: browserName = " + browserName);
		if (browserName == null || browserName.trim().length() == 0) {
			browserName = WebBrowserFactory.NAME_DEFAULT_BROWSER;
		}
		
		if (!this.browserName.equals(browserName)) {
			if (browser != null)
				dispose();

			final String finalBrowserName = browserName;

			AbstractJavaWebBrowser javaBrowser = null;
			if (createNew) {

				if (LOG.isDebugEnabled())
					LOG.debug("create New Browser: " + browserName);
				try {
					javaBrowser = WebBrowserFactory.newInstance(finalBrowserName);
				} catch (Throwable e) {
					LOG.warn(e.getMessage(),e);
				}
				browser = JavaWebBrowserImpl.newInstance(javaBrowser);
			} else {
				if (LOG.isTraceEnabled())
					LOG.trace("create Get Already exist Browser: " + browserName);
				try {
					javaBrowser = WebBrowserFactory.getInstance(finalBrowserName);
				} catch (Throwable e) {
					LOG.warn(e.getMessage(),e);
				}
				browser = JavaWebBrowserImpl.getInstance(javaBrowser);
			}
			browser.syncExec(new Runnable() {
				public void run() {
					browser.show(container);
				}
			});

			this.browserName = browserName;
		}
	}

	public JavaWebBrowserImpl getWebBrowser() {
		return browser;
	}

	public void dispose() {
		browser.dispose();
	}
}

package com.cic.datacrawl.core.browser;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.mozilla.javascript.xml.XMLObject;

import com.cic.datacrawl.core.browser.entity.BrowserDocument;
import com.cic.datacrawl.core.browser.entity.Cookie;
import com.cic.datacrawl.core.browser.entity.DomNode;
import com.cic.datacrawl.core.browser.entity.IFrameName;
import com.cic.datacrawl.core.browser.entity.NodePath;
import com.cic.datacrawl.core.browser.listener.RobotEditorListener;
import com.cic.datacrawl.core.browser.listener.SetURLListener;
import com.cic.datacrawl.core.util.URLUtil;

public class JavaWebBrowserImpl extends AbstractJavaWebBrowser {
	private static final Logger LOG = Logger.getLogger(JavaWebBrowserImpl.class);
	protected AbstractJavaWebBrowser browser;

	private JavaWebBrowserImpl(AbstractJavaWebBrowser browser) {
		init(browser);
	}

	private void init(AbstractJavaWebBrowser browser) {
		this.browser = browser;
	}

	private static HashMap<String, JavaWebBrowserImpl> browserMap = new HashMap<String, JavaWebBrowserImpl>();

	public AbstractJavaWebBrowser getBrowser() {
		return browser;
	}

	/**
	 * @return the timeout
	 */
	@Override
	public long getTimeout() {
		return browser.getTimeout();
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	@Override
	public void setTimeout(long timeout) {
		browser.setTimeout(timeout);
	}

	public static JavaWebBrowserImpl newInstance(AbstractJavaWebBrowser browser) {
		if (browser == null) {
			throw new NullPointerException("Initlized an invalid Java Browser.");
		}
		JavaWebBrowserImpl ret = new JavaWebBrowserImpl(browser);

		currentBrowser = ret;
		return ret;
	}

	public static JavaWebBrowserImpl getInstance(AbstractJavaWebBrowser browser) {
		if (browser == null) {
			throw new NullPointerException("Initlized an invalid Java Browser.");
		}
		JavaWebBrowserImpl ret = null;
		if (browserMap.containsKey(browser.getBrowserTypeValue())) {
			ret = browserMap.get(browser.getBrowserTypeValue());
		} else {
			ret = new JavaWebBrowserImpl(browser);
			browserMap.put(browser.getBrowserTypeValue(), ret);
		}
		currentBrowser = ret;
		return ret;
	}

	@Override
	public boolean back() {
		final boolean[] ret = new boolean[1];
		syncExec(new Runnable() {
			public void run() {
				browser.stop();
				ret[0] = browser.back();
			}
		});
		return ret[0];
	}

	@Override
	public void dispose() {
		syncExec(new Runnable() {
			public void run() {
				browser.dispose();
			}
		});
	}

	@Override
	public void ensureLoadComplete() {
		syncExec(new Runnable() {
			public void run() {
				browser.ensureLoadComplete();

				LOG.debug("Exit JavaWebBrowserImpl.ensureLoadComplete().run");
			}
		});
		LOG.debug("Exit JavaWebBrowserImpl.ensureLoadComplete()");
	}

	@Override
	public void execute(final String script) {
		syncExec(new Runnable() {
			public void run() {
				browser.execute(script);
			}
		});
	}

	@Override
	public boolean forward() {
		final boolean[] ret = new boolean[1];
		syncExec(new Runnable() {
			public void run() {
				browser.stop();
				ret[0] = browser.forward();
			}
		});
		return ret[0];
	}

	@Override
	public String getBrowserTypeValue() {
		final String[] ret = new String[1];
		syncExec(new Runnable() {
			public void run() {
				ret[0] = browser.getBrowserTypeValue();
			}
		});
		return ret[0];
	}

	@Override
	public DomNode getDocDomNode(final boolean makeSureStatus) {
		final DomNode[] ret = new DomNode[1];
		syncExec(new Runnable() {
			public void run() {
				ret[0] = browser.getDocDomNode(makeSureStatus);
			}
		});
		return ret[0];
	}

	@Override
	public String getHtmlSourceCode() {
		final String[] ret = new String[1];
		syncExec(new Runnable() {
			public void run() {
				ret[0] = browser.getHtmlSourceCode();
			}
		});
		return ret[0];
	}

	@Override
	public void highlightElement(final NodePath nodes, final int borderColor,
			final String backupAttributeName, final IFrameName frame) {
		syncExec(new Runnable() {
			public void run() {
				browser.highlightElement(nodes, borderColor, backupAttributeName, frame);
			}
		});
	}

	@Override
	public void highlightElements(final NodePath[] nodes, final int borderColor,
			final String backupAttributeName, final IFrameName frame) {
		syncExec(new Runnable() {
			public void run() {
				browser.highlightElements(nodes, borderColor, backupAttributeName, frame);
			}
		});
	}

	@Override
	public void initForRobotEditor(final RobotEditorListener robotEditorListener) {
		syncExec(new Runnable() {
			public void run() {
				browser.initForRobotEditor(robotEditorListener);
			}
		});
	}

	@Override
	public void invokeMethod(final Object methodObj, final String methodName) {
		syncExec(new Runnable() {
			public void run() {
				browser.invokeMethod(methodObj, methodName);
			}
		});
	}

	@Override
	public boolean isDefault() {
		final boolean[] ret = new boolean[1];
		syncExec(new Runnable() {
			public void run() {
				ret[0] = browser.isDefault();
			}
		});
		return ret[0];
	}

	@Override
	public void refresh() {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.stop();
				browser.refresh();
			}
		});
	}

	@Override
	public void setFlashEnabled(final boolean ifEnable) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.setFlashEnabled(ifEnable);
			}
		});
	}

	@Override
	public void setImageEnabled(final boolean ifEnable) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.setImageEnabled(ifEnable);
			}
		});
	}

	@Override
	public void setJavascriptEnabled(final boolean ifEnable) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.setJavascriptEnabled(ifEnable);
			}
		});
	}

	@Override
	public void setIFrameEnabled(final boolean ifEnable) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.setIFrameEnabled(ifEnable);
			}
		});
	}

	@Override
	public void show(final JPanel jpanel) {
		syncExec(new Runnable() {
			public void run() {
				browser.show(jpanel);
			}
		});
	}

	@Override
	public void stop() {
		syncExec(new Runnable() {
			public void run() {
				browser.stop();
			}
		});
	}

	@Override
	public void syncExec(Runnable runnable) {
		if (runnable != null)
			browser.syncExec(runnable);
	}

	@Override
	public void asyncExec(Runnable runnable) {
		if (runnable != null)
			browser.asyncExec(runnable);
	}

	@Override
	public void addCookie(String cookie) {
		browser.addCookie(cookie);
	}

	@Override
	public void addCookies(Cookie[] cookies) {
		browser.addCookies(cookies);
	}

	@Override
	public void setHtmlSourceCode(final String html) {
		syncExec(new Runnable() {

			@Override
			public void run() {
				browser.setHtmlSourceCode(html);
			}
		});
	}

	protected List<SetURLListener> setURLListenerList = new ArrayList<SetURLListener>();

	public void registerSetURLListener(SetURLListener listener) {
		synchronized (setURLListenerList) {
			setURLListenerList.add(listener);
		}
	}

	@Override
	public void elementClick(final XMLObject xmlObject) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.elementClick(xmlObject);
			}
		});
	}

	@Override
	public void elementSubmit(final XMLObject xmlObject) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.elementSubmit(xmlObject);
			}
		});
	}

	@Override
	public void elementSetChecked(final XMLObject xmlObject, final boolean bChecked) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.elementSetChecked(xmlObject, bChecked);
			}
		});
	}

	@Override
	public void elementSetValue(final XMLObject xmlObject, final String value) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.elementSetValue(xmlObject, value);
			}
		});
	}

	@Override
	public void downloadFile(final String imageURI, final String fileName) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.downloadFile(imageURI, fileName);
			}
		});
	}

	@Override
	public void setUrl(String url) {
		String theUrl = url;

		boolean hasListener = setURLListenerList.size() > 0;
		if (hasListener) {
			for (int i = 0; i < setURLListenerList.size(); ++i) {
				SetURLListener listener = setURLListenerList.get(i);
				if (listener != null) {

					try {
						listener.doBeforeUrlLocationChanged(theUrl);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		if (!URLUtil.isValid(theUrl)) {
			theUrl = "http://" + theUrl;
		}
		final String finalUrl = theUrl;
		browser.syncExec(new Runnable() {
			public void run() {
				// System.out.println((new
				// Date()).toString()+"set url: "+finalUrl);
				browser.setUrl(finalUrl);
			}
		});
		if (hasListener) {
			for (int i = 0; i < setURLListenerList.size(); ++i) {
				SetURLListener listener = setURLListenerList.get(i);
				if (listener != null) {
					try {
						listener.doAfterUrlLocationChanged(theUrl);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	@Override
	public Cookie[] getCookies() {
		final Cookie[][] result = new Cookie[1][];
		browser.syncExec(new Runnable() {
			public void run() {
				result[0] = browser.getCookies();
			}
		});
		return result[0];
	}

	@Override
	public int getLoadErrorCode() {
		browser.ensureLoadComplete();
		return browser.getLoadErrorCode();
	}

	@Override
	public int getHttpStatus() {
		final int[] ret = new int[1];
		browser.syncExec(new Runnable() {
			public void run() {
				// browser.ensureLoadComplete();
				if (LOG.isTraceEnabled()) {
					LOG.trace("complete load in getHttpStatus()");
				}
				ret[0] = browser.getHttpStatus();
			}
		});
		return ret[0];

	}

	@Override
	public String getUrl() {
		final String[] result = new String[1];
		browser.syncExec(new Runnable() {
			public void run() {
				result[0] = browser.getUrl();
			}
		});
		return result[0];
	}

	@Override
	public Image readImageData(final String imgElementId) {
		final Image[] result = new Image[1];
		browser.syncExec(new Runnable() {
			public void run() {
				result[0] = browser.readImageData(imgElementId);
			}
		});
		return result[0];
	}

	@Override
	public void changeCharset(String charset) {
		// commonBrowser.setCharset(charset);
	}

	@Override
	public IFrameName[] getAllIFrameNames(final boolean needRecursion) {
		final IFrameName[][] ret = new IFrameName[1][];
		syncExec(new Runnable() {
			public void run() {
				ret[0] = browser.getAllIFrameNames(needRecursion);
			}
		});
		return ret[0];
	}

	@Override
	public BrowserDocument getDocument() {
		final BrowserDocument[] result = new BrowserDocument[1];
		browser.syncExec(new Runnable() {
			public void run() {
				result[0] = browser.getDocument();
			}
		});
		return result[0];
	}

	@Override
	public void clearSessions() {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.clearSessions();
			}
		});
	}

	@Override
	public void setCacheEnabled(final boolean enabled) {
		browser.syncExec(new Runnable() {
			public void run() {
				browser.setCacheEnabled(enabled);
			}
		});
	}

	@Override
	public void addIgnoreURLPattern(Pattern ignoreURLPattern) {
		browser.addIgnoreURLPattern(ignoreURLPattern);
	}

	@Override
	public void clearIgnoreURLPattern() {
		browser.clearIgnoreURLPattern();
	}

}

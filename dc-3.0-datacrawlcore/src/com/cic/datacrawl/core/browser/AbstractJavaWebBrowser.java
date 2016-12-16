package com.cic.datacrawl.core.browser;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
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

public abstract class AbstractJavaWebBrowser {
	private static Logger LOG = Logger.getLogger(AbstractJavaWebBrowser.class);

	protected static AbstractJavaWebBrowser currentBrowser;

	public static AbstractJavaWebBrowser getCurrentBrowser() {
		return currentBrowser;
	}

	// protected volatile boolean loading; // 页面加载中
	// protected Object loadWaiter = new Object();

	protected volatile CountDownLatch latch;
	protected CountDownLatch ajaxLatch;
	protected Object loadWaiter;

	protected int loadErrorCode = Integer.MIN_VALUE;
	public final static int RESP_STATUS_TIMEOUT = 901;
	public static final int RESP_STATUS_NOT_ACCESS = 902;
	public final static String IFRAME_INDEX_SPLIT = "\\\\";

	protected volatile int responseStatus;
	protected String responseContentDisposition;
	protected String loadingUrl;

	protected RobotEditorListener robotEditorListener = null;
	protected boolean skipRobotEditorListener = true;

	private volatile long timeout = DEFAULT_TIME_OUT;

	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}

	private static final long DEFAULT_TIME_OUT = 60000;
	private static final long MIN_TIME_OUT = 30000;

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(long timeout) {
		if (!LOG.isDebugEnabled() && timeout < MIN_TIME_OUT)
			timeout = MIN_TIME_OUT;
		this.timeout = timeout;
	}

	public abstract String getBrowserTypeValue();

	public void addCookie(String cookie) {
		if (cookie != null) {
			cookie = cookie.trim();
			if (cookie.length() > 0) {
				synchronized (cookieList) {
					if (cookieList.contains(cookie)) {
						cookieList.add(cookie);
					}
				}
			}
		}
	}

	protected ArrayList<String> cookieList = new ArrayList<String>();

	public void addCookies(Cookie[] cookies) {
		if (cookies != null)
			for (int i = 0; i < cookies.length; ++i) {
				addCookie(cookies[i].toCookieString());
			}
	}

	/**
	 * 是否是默认的浏览器
	 * 
	 * @return
	 */
	public abstract boolean isDefault();

	/**
	 * 浏览器的停止功能
	 */
	public abstract void stop();

	/**
	 * 浏览器的后退功能
	 */
	public abstract boolean back();

	/**
	 * 浏览器的前进功能
	 */
	public abstract boolean forward();

	/**
	 * 浏览器的刷新功能
	 */
	public abstract void refresh();

	/**
	 * 禁用/启用 浏览器的javascript功能
	 * 
	 * @param ifEnable
	 */
	public abstract void setJavascriptEnabled(boolean ifEnable);

	/**
	 * 禁用/启用 浏览器的图片功能
	 * 
	 * @param ifEnable
	 */
	public abstract void setImageEnabled(boolean ifEnable);

	/**
	 * 禁用/启用 浏览器的Flash功能
	 * 
	 * @param ifEnable
	 */
	public abstract void setFlashEnabled(boolean ifEnable);

	/**
	 * 禁用/启用 浏览器的IFrame功能
	 * 
	 * @param ifEnable
	 */
	public abstract void setIFrameEnabled(boolean ifEnable);

	/**
	 * 等待页面加载完成
	 */
	public abstract void ensureLoadComplete();

	/**
	 * 加载页面成功返回0, 加载失败返回相应的错误号
	 * 
	 * @return
	 */
	public abstract int getLoadErrorCode();

	/**
	 * 获取应答状态号
	 * 
	 * @return
	 */
	public abstract int getHttpStatus();

	/**
	 * 获取当前所有cookie
	 * 
	 * @return
	 */
	public abstract Cookie[] getCookies();

	/**
	 * 获取页面中所有IFrame的名称
	 * 
	 * @param needRecursion
	 *            是否遍历所有层级的IFRAME
	 * @return
	 */
	public abstract IFrameName[] getAllIFrameNames(boolean needRecursion);

	// /**
	// * 获取Document的DOM节点树
	// *
	// * @param makeSureStatus
	// * 是否等待装载完成。<b>true</b>: 等待装载完成，<b>false</b>: 不等待装载完成
	// * @return
	// */
	// public abstract DomNode getIFrameDocDomNode(String frameName, int index);

	/**
	 * 获取Document的DOM节点树
	 * 
	 * @param makeSureStatus
	 *            是否等待装载完成。<b>true</b>: 等待装载完成，<b>false</b>: 不等待装载完成
	 * @return
	 */
	public abstract DomNode getDocDomNode(boolean makeSureStatus);

	/**
	 * 高亮显示指定元素
	 * 
	 * @param nodes
	 * @param borderColor
	 */
	public abstract void highlightElements(NodePath[] nodes, int borderColor, String backupAttributeName,
			IFrameName frame);

	/**
	 * 高亮显示指定元素
	 * 
	 * @param nodes
	 * @param borderColor
	 */
	public abstract void highlightElement(NodePath nodes, int borderColor, String backupAttributeName,
			IFrameName frame);

	/**
	 * 初始化RobotEditor支持
	 * 
	 * @param robotEditorListener
	 */
	public abstract void initForRobotEditor(RobotEditorListener robotEditorListener);

	/**
	 * 点击页面上的指定元素
	 * 
	 * @param xmlObject
	 *            通过E4X计算得到的XML元素
	 */
	public abstract void elementClick(XMLObject xmlObject);

	/**
	 * 页面form提交
	 * 
	 * @param xmlObject
	 */
	public abstract void elementSubmit(XMLObject xmlObject);

	/**
	 * 设置页面上的指定元素的值
	 * 
	 * @param xmlObject
	 *            通过E4X计算得到的XML元素
	 */
	public abstract void elementSetValue(XMLObject xmlObject, String value);

	public abstract void elementSetChecked(final XMLObject xmlObject, boolean bChecked);

	/**
	 * 将cache中的图片另存为文件
	 * 
	 * @param imgElementId
	 * @param fileName
	 */
	public abstract Image readImageData(String imgElementId);

	/**
	 * 利用WebBrowser进行文件下载
	 * 
	 * @param imageURI
	 * @param fileName
	 */
	public abstract void downloadFile(String imageURI, String fileName);

	public abstract void dispose();

	public abstract void execute(String script);

	public abstract void setHtmlSourceCode(String html);

	public abstract String getUrl();

	public abstract String getHtmlSourceCode();

	public abstract void setUrl(String url);

	public abstract void invokeMethod(Object methodObj, String methodName);

	//
	// void elementSetSelected(XMLObject xmlObject, boolean selected);

	/**
	 * 实现各自的事件异步访问回调。
	 * 
	 * @param runnable
	 *            待运行代码段
	 */
	public abstract void asyncExec(Runnable runnable);

	/**
	 * 实现各自的事件同步访问回调。
	 * 
	 * @param runnable
	 *            待运行代码段
	 */
	public abstract void syncExec(Runnable runnable);

	public void registerBrowserFunction(Object methodObj, String[] methodNames) {
		final Object finalMethodObj = methodObj;
		final String[] finalMethodNames = methodNames;
		// syncExec(new Runnable() {
		// public void run() {
		Class ownerClass = finalMethodObj.getClass();
		Class[] argsClass = new Class[] { Object[].class };
		for (int i = 0; i < finalMethodNames.length; ++i) {

			invokeMethod(finalMethodObj, finalMethodNames[i]);
		}
		// }
		// });
	}

	public void registerBrowserFunction(Object methodObj, String methodName) {
		registerBrowserFunction(methodObj, new String[] { methodName });
	}

	private HashMap<String, Integer> urlRequestMap = new HashMap<String, Integer>();

	protected volatile AtomicBoolean allRequestFinished = new AtomicBoolean(true);

	public boolean allRequestFinished() {
		//		
		// if(allRequestFinished.get()) {
		//			
		// return true;
		// }
		//		
		synchronized (urlRequestMap) {
			int size = urlRequestMap.size();

			if (LOG.isTraceEnabled()) {
				LOG.trace(">>>>>>>>>>>>> Start Testing Request >>>>>>>>>>>>>");
				LOG.trace("urlRequestMap.size() = " + size);
				if (size > 0) {
					Iterator<String> keyIterator = urlRequestMap.keySet().iterator();
					StringBuilder sb = new StringBuilder();
					boolean isNotFirstLine = false;
					while (keyIterator.hasNext()) {
						if (isNotFirstLine)
							sb.append("\n\t\t");
						String url = keyIterator.next();
						sb.append(url + ": " + urlRequestMap.get(url));
						isNotFirstLine = true;
					}
					LOG.trace(sb.toString());
				}

				LOG.trace("<<<<<<<<<<<<<< End Testing Request <<<<<<<<<<<<<<");
			}
			if (size == 0) {
				if (ajaxLatch != null) {
					ajaxLatch.countDown();
				}
				return true;
			} else {
				return false;
			}
			// return size == 0;
		}
	}

	/*
	 * //该方法用在得到load complete事件后，对url maps去除原始url的判断，主要是解决不能链接的页面 public boolean
	 * allRequestFinishedRemoveOrgUrl(String orgUrl) { // //
	 * if(allRequestFinished.get()) { // // return true; // } // synchronized
	 * (urlRequestMap) { urlRequestMap.remove(orgUrl); int size =
	 * urlRequestMap.size(); if (LOG.isDebugEnabled()) {
	 * LOG.debug(">>>>>>>>>>>>> Start Testing Request >>>>>>>>>>>>>");
	 * LOG.debug("urlRequestMap.size() = " + size); if (size > 0) {
	 * Iterator<String> keyIterator = urlRequestMap.keySet().iterator();
	 * StringBuilder sb = new StringBuilder(); boolean isNotFirstLine = false;
	 * while (keyIterator.hasNext()) { if (isNotFirstLine) sb.append("\n\t\t");
	 * String url = keyIterator.next(); sb.append(url + ": " +
	 * urlRequestMap.get(url)); isNotFirstLine = true; }
	 * LOG.debug(sb.toString()); }
	 * 
	 * LOG.debug("<<<<<<<<<<<<<< End Testing Request <<<<<<<<<<<<<<"); }
	 * if(size==0){ return true; }else{ return false; } //return size == 0; } }
	 */
	protected void addRequest(String url) {
		// Pattern pattern =
		// Pattern.compile("http://blog.sina.com.cn/s/comment_.+html");
		// Matcher matcher = pattern.matcher(url);
		// if (matcher.matches()) {
		// LOG.warn("------------------------------start add request--------------------------------");
		// LOG.warn("add request: url: " + url);
		// LOG.warn("------------------------------Finished add request--------------------------------");
		// }else {
		//
		// LOG.warn("//////////////////////////start add request////////////////////////");
		// LOG.warn("add request url: " + url);
		// LOG.warn("//////////////////////////Finished add request////////////////////////");
		// }

		synchronized (ignoreURLPatternList) {
			Iterator<Pattern> patternIterator = ignoreURLPatternList.values().iterator();
			while (patternIterator.hasNext()) {
				Pattern p = patternIterator.next();
				if (p.matcher(url).find()) {
					if (LOG.isTraceEnabled())
						LOG.trace(url + " is ignore url. match pattern : " + p.toString());
					return;
				}
			}
		}

		synchronized (urlRequestMap) {
			if (urlRequestMap.containsKey(url)) {
				int requestCounts = urlRequestMap.get(url).intValue() + 1;
				urlRequestMap.put(url, new Integer(requestCounts));

				if (LOG.isTraceEnabled())
					LOG.trace("addRequest: " + url + " number: " + requestCounts);
			} else {
				urlRequestMap.put(url, new Integer(1));

				if (LOG.isTraceEnabled())
					LOG.trace("addRequest: " + url + " number: 1");
			}
		}
	}

	protected void loggerAllTimeoutURL() {
		synchronized (urlRequestMap) {
			if (urlRequestMap.size() == 0) {
				LOG.trace("urlRequestMap.size = 0");
			} else {
				Iterator<String> keyIterator = urlRequestMap.keySet().iterator();

				int i = 0;
				while (keyIterator.hasNext()) {
					LOG.warn("In urlRequestMap, url[" + (i++) + "]: " + keyIterator.next());
				}
			}
		}
	}

	protected void resetRequestMap() {
		synchronized (urlRequestMap) {
			if (LOG.isTraceEnabled()) {
				if (urlRequestMap.size() == 0) {
					LOG.trace("urlRequestMap.size = 0");
				} else {
					LOG.trace("urlRequestMap.size = " + urlRequestMap.size());
					Iterator<String> keyIterator = urlRequestMap.keySet().iterator();

					int i = 0;
					while (keyIterator.hasNext()) {
						LOG.trace("url[" + (i++) + "] in urlRequestMap = " + keyIterator.next());
					}
				}
			}
			urlRequestMap.clear();
		}
		LOG.debug("urlRequestMap.clear() finished.");
	}

	protected void removeRequest(String url) {
		// Pattern pattern =
		// Pattern.compile("http://blog.sina.com.cn/s/comment_.+html");
		// Matcher matcher = pattern.matcher(url);
		// if (matcher.matches()) {
		// LOG.warn("------------------------------start remove request--------------------------------");
		// LOG.warn("remove request url: " + url);
		// LOG.warn("------------------------------Finished remove request--------------------------------");
		// } else {
		// LOG.warn("//////////////////////////start remove request////////////////////////");
		// LOG.warn("remove request url: " + url);
		// LOG.warn("//////////////////////////Finished remove request////////////////////////");
		// }
		synchronized (urlRequestMap) {
			if (urlRequestMap.containsKey(url)) {
				int requestCounts = urlRequestMap.get(url).intValue() - 1;
				if (requestCounts == 0) {
					urlRequestMap.remove(url);

					if(LOG.isTraceEnabled())
						LOG.trace("removeRequest: " + url + " number: 0");
				} else {
					urlRequestMap.put(url, new Integer(requestCounts));
					if(LOG.isTraceEnabled())
						LOG.trace("removeRequest: " + url + " number: " + requestCounts);
				}
			}
		}
	}

	public void show() {
		show(null);
	}

	public abstract void show(JPanel jPanel);

	public abstract void changeCharset(String charset);

	public abstract BrowserDocument getDocument();

	public abstract void clearSessions();

	public abstract void setCacheEnabled(boolean enabled);

	private HashMap<String, Pattern> ignoreURLPatternList = new HashMap<String, Pattern>();

	public List<Pattern> getIgnoreUrlPatternList() {
		ArrayList<Pattern> ret = new ArrayList<Pattern>();
		Iterator<String> keyIterator = ignoreURLPatternList.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			if (key != null)
				ret.add(ignoreURLPatternList.get(key));
		}
		return ret;
	}

	public void addIgnoreURLPattern(Pattern ignoreURLPattern) {
		synchronized (ignoreURLPatternList) {
			ignoreURLPatternList.put(ignoreURLPattern.toString(), ignoreURLPattern);

		}
	}

	public void clearIgnoreURLPattern() {
		synchronized (ignoreURLPatternList) {
			ignoreURLPatternList.clear();
		}
	}

}

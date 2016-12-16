package com.cic.datacrawl.core.browser;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.ProcessMonitor;
import com.cic.datacrawl.core.browser.entity.BrowserDocument;
import com.cic.datacrawl.core.browser.entity.Cookie;
import com.cic.datacrawl.core.browser.listener.SetURLListener;
import com.cic.datacrawl.core.browser.tools.ContentReplacement;
import com.cic.datacrawl.core.browser.tools.RhinoHttpClient;
import com.cic.datacrawl.core.util.StringUtil;

/* 线程安全的IWebBrowserEx封装实现, 及RhinoBrowser接口的实现 */
public class RhinoBrowserImpl implements RhinoBrowser {

	private static final Logger LOG = Logger.getLogger(RhinoBrowserImpl.class);
	private HashMap<String, ContentReplacement> contentReplacementMap = new HashMap<String, ContentReplacement>();

	private static RhinoBrowserImpl instance;

	protected AbstractJavaWebBrowser browser;

	private RhinoBrowserImpl(AbstractJavaWebBrowser browser) {
		init(browser);
	}

	private void init(AbstractJavaWebBrowser browser) {
		this.browser = browser;
	}

	protected List<SetURLListener> setURLListenerList = new ArrayList<SetURLListener>();

	public void registerSetURLListener(SetURLListener listener) {
		synchronized (setURLListenerList) {
			setURLListenerList.add(listener);
		}
	}

	@Override
	public void addContentReplacement(String regrex, String replaceTo) {

		synchronized (contentReplacementMap) {
			if (!contentReplacementMap.containsKey(regrex)) {
				contentReplacementMap.put(regrex, new ContentReplacement(regrex, replaceTo));
			}
		}
	}

	@Override
	public String getHtmlContent() {
		if (browser.getLoadErrorCode() != 0)
			return null;

		final String[] result = new String[1];

		result[0] = browser.getHtmlSourceCode();
		synchronized (contentReplacementMap) {
			if (contentReplacementMap.size() > 0) {
				Iterator<String> keyIterator = contentReplacementMap.keySet().iterator();

				while (keyIterator.hasNext()) {
					ContentReplacement replacement = contentReplacementMap.get(keyIterator.next());

					result[0] = replacement.doFilter(result[0]);
				}
			}
		}
		return result[0];
	}

	@Override
	public RhinoHttpClient getCookiedHttpClient() {
		RhinoHttpClient cookiedHttpClient = new RhinoHttpClient(this.getCookies());
		return cookiedHttpClient;
	}

	@Override
	public String getXmlContent() {
		if (browser.getLoadErrorCode() != 0)
			return null;

		String result = browser.getDocDomNode(true).toString();
		synchronized (contentReplacementMap) {
			if (contentReplacementMap.size() > 0) {
				Iterator<String> keyIterator = contentReplacementMap.keySet().iterator();

				while (keyIterator.hasNext()) {
					ContentReplacement replacement = contentReplacementMap.get(keyIterator.next());

					result = replacement.doFilter(result);
				}
			}
		}

		return result;
	}

	@Override
	public void jsExecute(final String script) {

		browser.execute(script);

	}

	@Override
	public void loadHtml(final String html, final String baseUrl) {

		browser.setHtmlSourceCode(html);

	}

	@Override
	public void removeAllContentReplacement() {
		synchronized (contentReplacementMap) {
			if (contentReplacementMap.size() > 0) {
				contentReplacementMap.clear();
			}
		}
	}

	@Override
	public void removeContentReplacement(String name) {
		synchronized (contentReplacementMap) {
			if (contentReplacementMap.containsKey(name)) {
				contentReplacementMap.remove(name);

			}
		}
	}

	private static HashMap<String, RhinoBrowserImpl> browserMap = new HashMap<String, RhinoBrowserImpl>();

	public static RhinoBrowserImpl newInstance(AbstractJavaWebBrowser browser) {
		if (browser == null) {
			throw new NullPointerException("Initlized an invalid Rhino Browser.");
		}
		instance = null;
		if (browserMap.containsKey(browser.getBrowserTypeValue())) {
			instance = browserMap.get(browser.getBrowserTypeValue());
		} else {
			instance = new RhinoBrowserImpl(browser);
			browserMap.put(browser.getBrowserTypeValue(), instance);
		}
		// 默认将全角空格全部替换为半角空格
		instance.addContentReplacement("　", " ");
		return instance;
	}

	public static RhinoBrowser getInstance() {
		return instance;
	}

	@Override
	public void addCookie(String cookie) {
		browser.addCookie(cookie);
	}

	@Override
	public void addCookie(Cookie cookie) {
		addCookie(new Cookie[] { cookie });
	}

	@Override
	public void addCookie(Cookie[] cookies) {
		browser.addCookies(cookies);
	}

	@Override
	public void setHtmlSourceCode(String html) {
		browser.setHtmlSourceCode(html);
	}

	@Override
	public void disableFlash() {
		browser.setFlashEnabled(false);
	}

	@Override
	public void disableImage() {
		browser.setImageEnabled(false);
	}

	@Override
	public void disableIFrame() {
		browser.setIFrameEnabled(false);
	}

	@Override
	public void disableJavascript() {
		browser.setJavascriptEnabled(true);
	}

	@Override
	public void enableIFrame() {
		browser.setIFrameEnabled(true);
	}

	@Override
	public void enableFlash() {
		browser.setFlashEnabled(true);
	}

	@Override
	public void enableImage() {
		browser.setImageEnabled(true);
	}

	@Override
	public void enableJavascript() {
		browser.setJavascriptEnabled(true);
	}

	@Override
	// added by steven
	public int refresh() {
		browser.refresh();
		return browser.getLoadErrorCode();
	}

	@Override
	public void downloadFile(String imageURI, String fileName) {
		browser.downloadFile(imageURI, fileName);
	}

	@Override
	public int setUrl(String url) {
		// 记录最后一次通过脚本改变url的时间，用来解决脚本执行过程中，长时间页面无响应的问题。
		ProcessMonitor.setChangeScriptStatusTime(System.currentTimeMillis());
		browser.setUrl(url);

		// System.gc();
		return browser.getLoadErrorCode();
		// return 0;
	}

	private Pattern buildPattern(Object pattern) {
		if (pattern == null) {
			return null;
		}
		if (pattern instanceof Pattern) {
			return (Pattern) pattern;
		}
		String patternStr = ObjectUtils.toString(pattern);
		patternStr = patternStr.trim();
		if (patternStr.length() == 0
			|| ObjectUtils.EMPTY_XML_OBJECT.equals(patternStr)
			|| ObjectUtils.NULL_OBJECT.equals(patternStr)
			|| ObjectUtils.UNDEFINED_OBJECT.equals(patternStr)) {

			return null;
		}
		Pattern p = null;
		boolean ignoreCase = false;
		String thePatternStr = patternStr;
		if (pattern instanceof NativeRegExp) {
			int lastSplitIndex = patternStr.lastIndexOf("/");
			thePatternStr = patternStr.substring(1, lastSplitIndex);
			if (lastSplitIndex != patternStr.length() - 1) {
				String controlString = patternStr.substring(lastSplitIndex);
				if (controlString.toLowerCase().indexOf("i") >= 0) {
					ignoreCase = true;
				}
			}
		}
		if (ignoreCase) {
			p = Pattern.compile(thePatternStr, Pattern.CASE_INSENSITIVE);
		} else {
			p = Pattern.compile(thePatternStr);
		}
		return p;

	}

	@Override
	public void addIgnoreURLPattern(Object urlPattern) {
		Pattern p = buildPattern(urlPattern);
		if (p != null)
			browser.addIgnoreURLPattern(p);
	}

	public void clearIgnoreURLPattern() {
		browser.clearIgnoreURLPattern();
	}

	@Override
	public Cookie[] getCookies() {
		return browser.getCookies();
	}

	@Override
	public int getErrorCode() {
		return browser.getLoadErrorCode();
	}

	@Override
	public int getHttpStatus() {
		return browser.getHttpStatus();
	}

	@Override
	public void elementClick(XMLObject xmlObject) {
		browser.elementClick(xmlObject);
	}

	@Override
	public void elementSubmit(XMLObject xmlObject) {

		browser.elementSubmit(xmlObject);
	}

	@Override
	public void elementSetChecked(XMLObject xmlObject, boolean bChecked) {
		browser.elementSetChecked(xmlObject, bChecked);
	}

	@Override
	public void elementSetValue(XMLObject xmlObject, String value) {
		browser.elementSetValue(xmlObject, value);
	}

	@Override
	public String getUrl() {
		return browser.getUrl();
	}

	@Override
	public Image readImageData(String imgElementId) {
		return browser.readImageData(imgElementId);
	}

	@Override
	public void setCharset(String charset) {
		// browser.setCharset(charset);
	}

	@Override
	public BrowserDocument getDocument() {
		BrowserDocument document = browser.getDocument();
		document.setBrowser(browser);
		document.setContentReplacementMap(contentReplacementMap);

		return document;
	}

	@Override
	public void setTimeout(long timeout) {
		browser.setTimeout(timeout);
	}

	@Override
	public void back() {
		browser.back();
	}

	@Override
	public void forward() {
		browser.forward();
	}

	@Override
	public String getDomain() {
		String url = getUrl().trim();

		if (StringUtil.isEmpty(url)) {
			return "";
		}
		if (url.toLowerCase().startsWith("http://")) {
			return url.substring(7, url.indexOf("/", 8));
		} else if (url.toLowerCase().startsWith("https://")) {
			return url.substring(8, url.indexOf("/", 9));
		} else {
			return url.substring(0, url.indexOf("/"));
		}
	}

	@Override
	public String getUrlPath() {
		String url = getUrl().trim();

		if (StringUtil.isEmpty(url)) {
			return "";
		}

		return url.substring(0, url.lastIndexOf("/"));
	}

	@Override
	public void clearCookies() {
		browser.clearSessions();
	}

	@Override
	public void disableCache() {
		browser.setCacheEnabled(false);
	}

	@Override
	public void enableCache() {
		browser.setCacheEnabled(true);

	}

	@Override
	public long getTimeout() {
		return browser.getTimeout();
	}


//	public static final int MONITOR_TYPE_DEFAULT = AbstractJavaWebBrowser.MONITOR_TYPE_DEFAULT;
//	public static final int MONITOR_TYPE_ONLY_ALLOW_WHITE_LIST = AbstractJavaWebBrowser.MONITOR_TYPE_ONLY_ALLOW_WHITE_LIST;



//	@Override
//	public void addValidURLPattern(Object urlPattern) {
//		Pattern p = buildPattern(urlPattern);
//		if (p != null)
//			browser.addValidURLPattern(p);
//	}
//
//
//
//
//	public void clearValidURLPattern() {
//		browser.clearValidURLPattern();
//	}	
//
//	public void setURLMonitorType(int type){
//		browser.setURLMonitorType(type);
//	}
//
//	public int getURLMonitorType(){
//		return browser.getURLMonitorType();
//	}
}

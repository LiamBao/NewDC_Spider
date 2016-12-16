package com.cic.datacrawl.core.browser.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;
import com.cic.datacrawl.ui.beans.BrowserDefination;

public class BrowserList {
	private static final Logger LOG = Logger.getLogger(BrowserList.class);
	private Map<String, AbstractJavaWebBrowser> browserMap = new HashMap<String, AbstractJavaWebBrowser>();
	private List<BrowserDefination> browserDefinationList = new ArrayList<BrowserDefination>();

	/**
	 * @return the browserDefinationList
	 */
	public List<BrowserDefination> getBrowserDefinationList() {
		return browserDefinationList;
	}

	/**
	 * @param browserDefinationList
	 *            the browserDefinationList to set
	 */
	public void setBrowserDefinationList(List<BrowserDefination> browserDefinationList) {
		this.browserDefinationList = browserDefinationList;
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

	public AbstractJavaWebBrowser createAndAddBrowser(String browserName, String className, List<String> browserIgnoreUrlList)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		AbstractJavaWebBrowser ret = (AbstractJavaWebBrowser) Class.forName(className).newInstance();
		
		//TODO addIgnoreURLPattern(ignoreURLPattern)
		//ret.addIgnoreURLPattern(ignoreURLPattern)
		if(browserIgnoreUrlList!=null){
			for(int i=0;i<browserIgnoreUrlList.size();i++){
				//System.out.println("xxxx"+browserIgnoreUrlList.get(i));
				Pattern p = buildPattern(browserIgnoreUrlList.get(i));
				if (p != null){
					ret.addIgnoreURLPattern(p);
				}
			}
		}
		
		
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Create Browser: " + className);
		}
		synchronized (browserMap) {
			browserMap.put(browserName, ret);
		}
		return ret;
	}

	public AbstractJavaWebBrowser getBrowser(String browserName) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		if (browserMap.containsKey(browserName)) {
			return (AbstractJavaWebBrowser) browserMap.get(browserName);
		} else {
			return createAndAddBrowser(browserName, getDefination(browserName).getClassName(), getDefination(browserName).getBrowserIgnoreUrlList());
		}
	}

	protected BrowserDefination getDefaultDefination() {
		BrowserDefination ret = null;
		if (LOG.isDebugEnabled())
			LOG.debug("browserDefinationList.size() = " + browserDefinationList.size());
		for (int i = 0; i < browserDefinationList.size(); ++i) {
			BrowserDefination obj = browserDefinationList.get(i);

			if (i == 0)
				ret = obj;

			if (obj.isDefault()) {
				if (LOG.isDebugEnabled())
					LOG.debug("return Default Defination: " + ret);
				return obj;
			}
		}
		if (LOG.isDebugEnabled())
			LOG.debug("return " + ret);
		return ret;
	}

	protected BrowserDefination getDefination(String name) {
		if (name == null)
			return null;
		for (int i = 0; i < browserDefinationList.size(); ++i) {
			BrowserDefination obj = browserDefinationList.get(i);

			if (name.equals(obj.getName())) {
				return obj;
			}
		}
		return null;
	}

	public AbstractJavaWebBrowser getDefalutBrowser() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		if (browserDefinationList == null || browserDefinationList.size() == 0)
		{
		    LOG.info("browserDefinationList is null ");
		    return null;
		}
		for (int i = 0; i < browserDefinationList.size(); ++i) {
			BrowserDefination obj = browserDefinationList.get(i);
			LOG.info("BrowserDefination is default: " + obj.isDefault());
			if (obj.isDefault()) {
				AbstractJavaWebBrowser ret = null;
				if (browserMap.containsKey(obj.getName())) {
					ret = getBrowser(obj.getName());
				} else {
					ret = createAndAddBrowser(obj.getName(), obj.getClassName(),obj.getBrowserIgnoreUrlList());
				}
				return ret;
			}
		}
		return null;
	}

}

package com.cic.datacrawl.core.browser.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;
import com.cic.datacrawl.core.browser.tools.ContentReplacement;
import com.cic.datacrawl.core.util.StringUtil;

public abstract class BrowserDocument {

	protected static final Logger LOG = Logger.getLogger(BrowserDocument.class);

	protected HashMap<String, ContentReplacement> contentReplacementMap = new HashMap<String, ContentReplacement>();

	/**
	 * @return the contentReplacementMap
	 */
	protected HashMap<String, ContentReplacement> getContentReplacementMap() {
		return contentReplacementMap;
	}

	private AbstractJavaWebBrowser browser;

	protected AbstractJavaWebBrowser getBrowser() {
		return browser;
	}

	/**
	 * @param contentReplacementMap
	 *            the contentReplacementMap to set
	 */
	public void setContentReplacementMap(HashMap<String, ContentReplacement> contentReplacementMap) {
		this.contentReplacementMap = contentReplacementMap;
	}

	/**
	 * @param browser
	 *            the browser to set
	 */
	public void setBrowser(AbstractJavaWebBrowser browser) {
		this.browser = browser;
	}

	protected String filterString(String str) {
		synchronized (contentReplacementMap) {
			if (contentReplacementMap.size() > 0) {
				Iterator<String> keyIterator = contentReplacementMap.keySet().iterator();

				while (keyIterator.hasNext()) {
					ContentReplacement replacement = contentReplacementMap.get(keyIterator.next());

					str = replacement.doFilter(str);
				}
			}
		}
		return str;
	}

	/**
	 * @return the xmlContent
	 */
	public abstract String getXmlContent();

	// {
	// if (xmlContent == null) {
	// xmlContent = browser.getDocDomNode(true).toString();
	// synchronized (contentReplacementMap) {
	// if (contentReplacementMap.size() > 0) {
	// Iterator<String> keyIterator = contentReplacementMap.keySet().iterator();
	//
	// while (keyIterator.hasNext()) {
	// ContentReplacement replacement =
	// contentReplacementMap.get(keyIterator.next());
	//
	// xmlContent = replacement.doFilter(xmlContent);
	// }
	// }
	// }
	// }
	//
	// return xmlContent;
	// }

	/**
	 * @return the htmlContent
	 */
	public abstract String getHtmlContent();

	// {
	// if (htmlContent == null) {
	// LOG.debug("Initial htmlContent");
	// htmlContent = browser.getHtmlSourceCode();
	// }
	// return htmlContent;
	// }

	private ArrayList<String> frameNameList = new ArrayList<String>();;

	private ArrayList<IFrameElement> iframeList = new ArrayList<IFrameElement>();
	private HashMap<String, Integer> nameLocationMap = new HashMap<String, Integer>();

	public boolean hasIFrame() {
		return iframeList.size() > 0;
	}

	public void setFrameNames(String[] frameNames) {
		if (frameNames != null) {
			for (int i = 0; i < frameNames.length; ++i) {
				String frameName = frameNames[i];
				if (!frameNameList.contains(frameName)) {
					frameNameList.add(frameName);
				}
			}
		}
	}

	public String[] getAllIFrameNames() {
		String[] ret = new String[frameNameList.size()];
		frameNameList.toArray(ret);
		return ret;
	}

	public IFrameElement getFrame(String name) {
		if (StringUtil.isEmpty(name)) {
			return null;
		}
		if (!frameNameList.contains(name)) {
			if (LOG.isTraceEnabled())
				LOG.trace("Frame[" + name + "] is not in the document.");
			return null;
		}

		if (nameLocationMap.containsKey(name)) {
			return iframeList.get(nameLocationMap.get(name));
		}
		IFrameElement frameElement = createFrameElement(name);
		addIFrame(name, frameElement);

		return frameElement;
	}

	protected abstract IFrameElement createFrameElement(String name);

	public void addIFrame(String name, IFrameElement iframe) {
		synchronized (iframeList) {

			iframeList.add(iframe);
			int index = iframeList.size() - 1;

			nameLocationMap.put(name, index);
		}
	}
}

package com.cic.datacrawl.core.browser.entity;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;
import com.cic.datacrawl.core.browser.tools.ContentReplacement;

public abstract class IFrameElement {
	private static final Logger LOG = Logger.getLogger(IFrameElement.class);
	
	private String name;
	private String id;
	private String src;
	private BrowserDocument document;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @param src
	 *            the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	
	/**
	 * @return the document
	 */
	public BrowserDocument getDocument() {
		if(document == null){
			document = initIFrameDocument();
			document.setBrowser(browser);
			document.setContentReplacementMap(contentReplacementMap);
		}
		return document;
	}

	private HashMap<String, ContentReplacement> contentReplacementMap = new HashMap<String, ContentReplacement>();

	private AbstractJavaWebBrowser browser;

	/**
	 * @param contentReplacementMap
	 *            the contentReplacementMap to set
	 */
	public void setContentReplacementMap(
			HashMap<String, ContentReplacement> contentReplacementMap) {
		this.contentReplacementMap = contentReplacementMap;
	}

	/**
	 * @param browser
	 *            the browser to set
	 */
	public void setBrowser(AbstractJavaWebBrowser browser) {
		this.browser = browser;
	}

	protected abstract BrowserDocument initIFrameDocument();

}

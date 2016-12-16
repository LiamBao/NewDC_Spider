package com.cic.datacrawl.core.browser;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.internal.mozilla.XPCOM;
import org.mozilla.interfaces.nsIComponentManager;
import org.mozilla.interfaces.nsIComponentRegistrar;
import org.mozilla.interfaces.nsICookie;
import org.mozilla.interfaces.nsICookieManager;
import org.mozilla.interfaces.nsIDOMAbstractView;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentEvent;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMHTMLElement;
import org.mozilla.interfaces.nsIDOMHTMLFormElement;
import org.mozilla.interfaces.nsIDOMHTMLFrameElement;
import org.mozilla.interfaces.nsIDOMHTMLIFrameElement;
import org.mozilla.interfaces.nsIDOMHTMLInputElement;
import org.mozilla.interfaces.nsIDOMHTMLOptionElement;
import org.mozilla.interfaces.nsIDOMHTMLSelectElement;
import org.mozilla.interfaces.nsIDOMHTMLTextAreaElement;
import org.mozilla.interfaces.nsIDOMMouseEvent;
import org.mozilla.interfaces.nsIDOMNSHTMLButtonElement;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIDownloadManager;
import org.mozilla.interfaces.nsIFactory;
import org.mozilla.interfaces.nsIHttpChannel;
import org.mozilla.interfaces.nsIIOService;
import org.mozilla.interfaces.nsILocalFile;
import org.mozilla.interfaces.nsIObserver;
import org.mozilla.interfaces.nsIObserverService;
import org.mozilla.interfaces.nsIPrefBranch;
import org.mozilla.interfaces.nsIPrefService;
import org.mozilla.interfaces.nsIPromptService2;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.interfaces.nsISimpleEnumerator;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebBrowserPersist;
import org.mozilla.interfaces.nsIWebBrowserSetup;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.RhinoXmlUtil;
import org.mozilla.xpcom.IXPCOMError;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Node;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.browser.entity.BrowserDocument;
import com.cic.datacrawl.core.browser.entity.Cookie;
import com.cic.datacrawl.core.browser.entity.DomNode;
import com.cic.datacrawl.core.browser.entity.IFrameName;
import com.cic.datacrawl.core.browser.entity.NodePath;
import com.cic.datacrawl.core.browser.entity.SWTBrowserDocument;
import com.cic.datacrawl.core.browser.entity.SWTIFrameName;
import com.cic.datacrawl.core.browser.listener.RobotEditorListener;
import com.cic.datacrawl.core.browser.tools.MozillaPromptService2;
import com.cic.datacrawl.core.browser.tools.UIOperation;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.system.SystemInterface;
import com.cic.datacrawl.core.util.ArrayUtil;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.core.util.URLUtil;

public class SWTMozilla extends AbstractJavaSWTWebBrowser {

	private static final Logger LOG = Logger.getLogger(SWTMozilla.class);

	public SWTMozilla() {
		super();
		defaultInstance = this;
	}

	private boolean enableImage = true;
	private boolean enableFlash = true;
	private boolean enableJavaScript = true;
	// private boolean httpMonitorObserved;

	private static SWTMozilla defaultInstance;

	/**
	 * @return the defaultInstance
	 */
	public static SWTMozilla getDefaultInstance() {
		return defaultInstance;
	}

	private boolean defaultValue;

	/**
	 * @return the defaultValue
	 */
	public boolean getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the default
	 */
	@Override
	public boolean isDefault() {
		return defaultValue;
	}

	/**
	 * @param default1
	 *            the default to set
	 */
	public void setDefaultValue(boolean isDefault) {
		defaultValue = isDefault;
	}

	@Override
	public void setFlashEnabled(boolean ifEnable) {
		enableFlash = ifEnable;
		// 启用/禁用所有插件
		nsIWebBrowser webBrowser = (nsIWebBrowser) browser.getWebBrowser();
		nsIWebBrowserSetup wbSetup = qi(webBrowser, nsIWebBrowserSetup.class);
		wbSetup.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_PLUGINS,
				ifEnable ? 1 : 0);
	}

	@Override
	public void setCacheEnabled(boolean enabled) {
		if (pref == null)
			pref = getService("@mozilla.org/preferences-service;1",
					nsIPrefBranch.class);

		pref.setBoolPref("nglayout.debug.disable_xul_cache", enabled ? 0 : 1);
	}

	@Override
	public void setImageEnabled(boolean ifEnable) {
		enableImage = ifEnable;
		nsIWebBrowser webBrowser = (nsIWebBrowser) browser.getWebBrowser();
		nsIWebBrowserSetup wbSetup = qi(webBrowser, nsIWebBrowserSetup.class);
		wbSetup.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_IMAGES, ifEnable ? 1
				: 0);
	}

	@Override
	public void setJavascriptEnabled(boolean ifEnable) {
		enableJavaScript = ifEnable;
		browser.setJavascriptEnabled(ifEnable);
	}

	@Override
	public void setIFrameEnabled(boolean ifEnable) {
		nsIWebBrowser webBrowser = (nsIWebBrowser) browser.getWebBrowser();
		nsIWebBrowserSetup wbSetup = qi(webBrowser, nsIWebBrowserSetup.class);
		wbSetup.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_SUBFRAMES,
				ifEnable ? 1 : 0);
	}

	@Override
	public void ensureLoadComplete() {
		// boolean timeout = waitLoad(2);
		// System.out.println("timeout:"+getTimeout());
		long starttime = System.currentTimeMillis();
		if (LOG.isTraceEnabled())
			LOG.trace("getTimeout() = " + getTimeout());
		boolean timeout = false;
		try {
			synchronized (latch) {
				timeout = !latch.await(getTimeout(), TimeUnit.MILLISECONDS);// waitLoad(getTimeout());
			}
		} catch (Exception e) {
			if (isloadCurrentURLComplete.get()) {
				if (latch != null) {
					latch.countDown();
				}
				executeOnLoadComplete();
			} else {
				timeout = true;
			}
		}

		// 如果超时了。则判断是否所有的url都加载完成，如果加载完成则设置loaderrorcode为0，否则关闭browser，设置loaderrorcode为超时
		if (timeout) {
			loggerAllTimeoutURL();
			final CountDownLatch stopLatch = new CountDownLatch(1);

			syncExec(new Runnable() {

				@Override
				public void run() {
					browser.stop();

					stopLatch.countDown();
				}

			});
			// Waits for the loading is stopped
			boolean stopTimeOut = false;
			try {
				stopTimeOut = !stopLatch.await(getTimeout(),
						TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			// finally reset the requestMap
			resetRequestMap();
			if (stopTimeOut)
				this.loadErrorCode = RESP_STATUS_TIMEOUT;// 设置超时

		} else {
			// 首先判断，是不是加载了系统文件，如果是，则loadErrorCode=0，并且直接返回
			// System.out.println(this.loadingUrl);
			if (this.loadingUrl.startsWith("jar:file")) {

				this.loadErrorCode = 0;
				resetRequestMap();

				allRequestFinished.set(true);

				return;
			}

			// 如果加载完成，则判断是否所有的url都完成了。

			if (allRequestFinished()) {
				// 如果所有的url都加载完成
				this.loadErrorCode = 0;
				// System.out.println((new Date()).toString()
				// + "--------------all request finished-----------------");
			} else {
				// System.out.println((new Date()).toString()
				// +
				// "--------------all request not finished maybe some ajax request-----------------");
				// 如果没有加载完成，则等待其他的加载完成
				ajaxLatch = new CountDownLatch(1);

				// 每隔1秒钟，判断加载是否完成,检测线程
				new Thread(new Runnable() {

					@Override
					public void run() {
						do {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						} while (!allRequestFinished());
					}

				}).start();

				try {
					// System.out.println((new Date()).toString()
					// +
					// "----------------ajax time out----------panduan-------------- ");
					boolean ajaxTimeout = !ajaxLatch.await(getTimeout(),
							TimeUnit.MILLISECONDS);

					if (ajaxTimeout) {
						// 如果超时

						final CountDownLatch stopLatch = new CountDownLatch(1);

						syncExec(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								browser.stop();

								stopLatch.countDown();
							}

						});
						// // Waits for the loading is stopped
						boolean stopTimeOut = false;
						try {

							stopTimeOut = !stopLatch.await(getTimeout(),
									TimeUnit.MILLISECONDS);

						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						resetRequestMap();
						allRequestFinished.set(true);
						if (stopTimeOut)
							this.loadErrorCode = RESP_STATUS_TIMEOUT;// 设置超时
					} else {
						// finally reset the requestMap
						resetRequestMap();
						allRequestFinished.set(true);
						this.loadErrorCode = 0;

					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

			}

		}

		LOG.debug("Log End ============ End of ensureLoadComplete");

	}

	@Override
	public void downloadFile(String imageURI, String fileName) {
		nsIComponentManager componentManager = org.mozilla.xpcom.Mozilla
				.getInstance().getComponentManager();
		nsIServiceManager serviceManager = org.mozilla.xpcom.Mozilla
				.getInstance().getServiceManager();
		nsIWebBrowserPersist persist = (nsIWebBrowserPersist) componentManager
				.createInstanceByContractID(
						"@mozilla.org/embedding/browser/nsWebBrowserPersist;1",
						null, nsIWebBrowserPersist.NS_IWEBBROWSERPERSIST_IID);
		nsIIOService ioService = (nsIIOService) serviceManager
				.getServiceByContractID("@mozilla.org/network/io-service;1",
						nsIIOService.NS_IIOSERVICE_IID);

		nsILocalFile localFile = (nsILocalFile) componentManager
				.createInstanceByContractID("@mozilla.org/file/local;1", null,
						nsILocalFile.NS_ILOCALFILE_IID);
		localFile.initWithPath(fileName);

		nsIURI uri = ioService.newURI(imageURI, null, null);
		persist.setPersistFlags(nsIWebBrowserPersist.PERSIST_FLAGS_FROM_CACHE);
		this.loadingUrl = imageURI; // 用于Http Monitor
		persist.saveURI(uri, null, null, null, null, localFile);
	}

	private String formatScript(String scriptStr, nsIDOMHTMLElement htmlElement) {
		String jsStart = "javascript:";
		String vbStart = "vbscript:";
		int jsStartIndex = scriptStr.toLowerCase().indexOf(jsStart);
		if (jsStartIndex >= 0) {
			scriptStr = scriptStr.substring(jsStartIndex + jsStart.length());
		} else {
			int vbStartIndex = scriptStr.toLowerCase().indexOf(vbStart);
			if (vbStartIndex >= 0) {
				scriptStr = scriptStr
						.substring(vbStartIndex + vbStart.length());
			}
		}
		scriptStr = StringUtil.replaceAll(scriptStr, "return false;", "");
		scriptStr = StringUtil.replaceAll(scriptStr, "return false", "");
		scriptStr = scriptStr.trim();
		if (scriptStr.startsWith("return ")) {
			scriptStr = scriptStr.substring("return".length()).trim();
		}
		scriptStr = replaceThisValue(scriptStr, htmlElement);

		return scriptStr;
	}

	private String replaceThisValue(String scriptStr,
			nsIDOMHTMLElement htmlElement) {
		if (htmlElement == null)
			return scriptStr;
		int indexThis = -1;
		int end = -1;
		boolean isFirstLoop = true;
		StringBuilder ret = new StringBuilder();
		while ((indexThis = indexThis < 0 ? scriptStr.indexOf("this.")
				: scriptStr.indexOf("this.", indexThis)) >= 0) {
			if (indexThis < 0)
				break;
			if (isFirstLoop)
				ret.append(scriptStr.substring(0, indexThis));
			else
				ret.append(scriptStr.substring(end, indexThis));

			indexThis = indexThis += 5;
			int indexRightComma = scriptStr.indexOf(",", indexThis);
			if (indexRightComma > 0) {
				end = indexRightComma;
			} else {
				end = scriptStr.indexOf(")", indexThis);
			}
			String variableName = scriptStr.substring(indexThis, end).trim();
			ret.append("\"");
			String variableValue = htmlElement.getAttribute(variableName);
			ret.append(variableValue);
			ret.append("\"");
			isFirstLoop = false;
		}
		if (end > 0)
			ret.append(scriptStr.substring(end));
		// System.out.println(ret.toString());
		return ret.toString();
	}

	private static String guessIID(Class c) {
		try {
			String name = c.getName();
			String baseName = c.getSimpleName();
			final String iidFieldName;
			if (name.startsWith("org.mozilla.interfaces.ns")) { //$NON-NLS-1$ 
				iidFieldName = String.format(
						"NS_%s_IID", baseName.substring(2).toUpperCase()); //$NON-NLS-1$ 
			} else {
				iidFieldName = String.format("%s_IID", baseName.toUpperCase()); //$NON-NLS-1$ 
			}

			Field f = c.getDeclaredField(iidFieldName);
			String iid = (String) f.get(c);
			return iid;
		} catch (Throwable e) {
			LOG.error("failed to resolve IID of an XPCOM interface", e); //$NON-NLS-1$ 
			return null;
		}
	}

	/**
	 * Returns XPCOM service with the given contract ID. This method is an
	 * equivalent of the <tt>do_GetService()</tt> macro in mozilla source code.
	 * For example,
	 * 
	 * <pre>
	 * getService(&quot;@mozilla.org/cookiemanager;1&quot;, nsICookieManager.class)
	 * </pre>
	 * 
	 * returns the <tt>nsICookieManager</tt> interface of the Mozilla's cookie
	 * service. Similarly,
	 * 
	 * <pre>
	 * getService(&quot;@mozilla.org/cookiemanager;1&quot;, nsICookieService.class)
	 * </pre>
	 * 
	 * returns the <tt>nsICookieService</tt> interface of the cookie service.
	 * 
	 * @param <T>
	 *            - an XPCOM interface (org.mozilla.interfaces)
	 * @param contractID
	 *            - identifier of the XPCOM service
	 * @param c
	 *            - interface of the XPCOM service to be returned
	 * @return XPCOM service
	 */
	@SuppressWarnings( { "unchecked" })
	public static <T extends nsISupports> T getService(String contractID,
			Class<T> c) {
		try {
			Mozilla moz = Mozilla.getInstance();
			String iid = guessIID(c);
			nsIServiceManager serviceManager = moz.getServiceManager();

			T t = (T) serviceManager.getServiceByContractID(contractID, iid);

			return t;
		} catch (Throwable e) {
			LOG.error("failed to create XPCOM service", e); //$NON-NLS-1$
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends nsISupports> T qi(nsISupports obj, Class<T> c) {
		try {
			if (obj == null)
				return null;
			String iid = guessIID(c);
			T t = (T) obj.queryInterface(iid);
			return t;
		} catch (XPCOMException e) {
			// do not print an error if
			// obj does not implement the interface
			if (e.errorcode != IXPCOMError.NS_ERROR_NO_INTERFACE) {
				LOG.error("failed to query-interface an XPCOM object", e); //$NON-NLS-1$ 
			}
			return null;
		} catch (Throwable e) {
			LOG.error("failed to query-interface an XPCOM object", e); //$NON-NLS-1$ 
			return null;
		}
	}

	@Override
	public void elementClick(XMLObject xmlObject) {
		nsIDOMHTMLElement htmlElement = findHtmlElement(xmlObject);
		boolean needInitMouseEvent = false;
		try {
			nsIDOMNSHTMLButtonElement element = qi(htmlElement,
					nsIDOMNSHTMLButtonElement.class);
			if (element != null) {
				element.click();
			} else {
				needInitMouseEvent = true;
			}
		} catch (Exception e) {
			try {
				nsIDOMHTMLOptionElement element = qi(htmlElement,
						nsIDOMHTMLOptionElement.class);
				if (element != null) {
					element.setSelected(!element.getSelected());

				} else {
					needInitMouseEvent = true;
				}
			} catch (Exception e1) {
				needInitMouseEvent = true;
			}
		}
		if (needInitMouseEvent) {
			nsIDOMDocument doc = htmlElement.getOwnerDocument();
			nsIDOMDocumentEvent evdoc = qi(doc, nsIDOMDocumentEvent.class);
			nsIDOMEvent ev = evdoc.createEvent("MouseEvents"); //$NON-NLS-1$ 
			nsIDOMMouseEvent mev = qi(ev, nsIDOMMouseEvent.class);
			nsIDOMDocumentView view = qi(doc, nsIDOMDocumentView.class);
			nsIDOMAbstractView aview = view.getDefaultView();
			mev
					.initMouseEvent(
							"click", true, true, aview, 0, 0, 0, 0, 0, false, false, false, false, 0, null); //$NON-NLS-1$ 
			nsIDOMEventTarget evt = qi(htmlElement, nsIDOMEventTarget.class);
			evt.dispatchEvent(mev);
		}
	}

	@Override
	public void elementSubmit(XMLObject xmlObject) {
		nsIDOMHTMLElement htmlElement = findHtmlElement(xmlObject);

		String tagName = htmlElement.getTagName();
		if ("FORM".equals(tagName)) {
			nsIDOMHTMLFormElement formElement = qi(htmlElement,
					nsIDOMHTMLFormElement.class);
			formElement.submit();
			return;
		}

		throw new RuntimeException("Element type <" + htmlElement.getTagName()
				+ "> doesn't support submit()");
	}

	// @Override
	// public void elementSetSelected(XMLObject xmlObject, boolean selected) {
	// nsIDOMHTMLElement htmlElement = findHtmlElement(xmlObject);
	// try {
	// nsIDOMHTMLOptionElement element = (nsIDOMHTMLOptionElement) htmlElement
	// .queryInterface(nsIDOMHTMLOptionElement.NS_IDOMHTMLOPTIONELEMENT_IID);
	// if (element != null) {
	// element.setSelected(selected);
	// }
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// throw new RuntimeException("Element type <" + htmlElement.getTagName()
	// + "> doesn't support elementSetSelected(...)");
	// }

	@Override
	public void elementSetChecked(XMLObject xmlObject, boolean bChecked) {
		nsIDOMHTMLElement htmlElement = findHtmlElement(xmlObject);
		nsIDOMHTMLInputElement inputElement = qi(htmlElement,
				nsIDOMHTMLInputElement.class);
		if (inputElement != null) {
			String inputType = inputElement.getType();
			if ("radio".equalsIgnoreCase(inputType)
					|| "checkbox".equalsIgnoreCase(inputType))
				inputElement.setChecked(bChecked);
			else
				throw new RuntimeException("InputElement type <" + inputType
						+ "> doesn't support elementSetChecked(...)");
			return;
		}

		throw new RuntimeException("Element type <" + htmlElement.getTagName()
				+ "> doesn't support elementSetChecked(...)");
	}

	@Override
	public void elementSetValue(XMLObject xmlObject, String value) {
		nsIDOMHTMLElement htmlElement = findHtmlElement(xmlObject);
		String tagName = htmlElement.getTagName();
		if ("SELECT".equalsIgnoreCase(tagName)) {
			nsIDOMHTMLSelectElement selectElement = qi(htmlElement,
					nsIDOMHTMLSelectElement.class);
			if (selectElement != null) {
				selectElement.setValue(value);
				return;
			}
		} else if ("INPUT".equalsIgnoreCase(tagName)) {
			nsIDOMHTMLInputElement inputElement = qi(htmlElement,
					nsIDOMHTMLInputElement.class);
			if (inputElement != null) {
				inputElement.setValue(value);
				return;
			}
		} else if ("TEXTAREA".equalsIgnoreCase(tagName)) {
			nsIDOMHTMLTextAreaElement textAreaElement = qi(htmlElement,
					nsIDOMHTMLTextAreaElement.class);
			if (textAreaElement != null) {

				nsIDOMDocument document = getNsIDOMDocument();

				nsIDOMNodeList nodeList = textAreaElement.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					textAreaElement.removeChild(nodeList.item(i));
				}

				textAreaElement.appendChild(document.createTextNode(value));
				return;
			}
		}

		throw new RuntimeException("Element type <" + htmlElement.getTagName()
				+ "> doesn't support setValue(...)");
	}

	nsIDOMHTMLElement findHtmlElement(XMLObject xmlObject) {
		Node[] w3cNodes = RhinoXmlUtil.toXmlNodes(xmlObject);
		if (w3cNodes == null)
			throw new RuntimeException("No element be found.");
		if (w3cNodes.length == 0)
			throw new RuntimeException("No element be found.");
		if (w3cNodes.length > 1)
			throw new RuntimeException("More than one element be found.");

		nsIDOMDocument document = getNsIDOMDocument();

		nsIDOMNode domNode = findHtmlDomByXmlNode(document, NodePath
				.w3cNodePath(w3cNodes[0]));
		if (domNode == null)
			throw new RuntimeException("No html node match this xml node.");
		nsIDOMHTMLElement htmlElement = qi(domNode, nsIDOMHTMLElement.class);

		if (htmlElement == null)
			throw new RuntimeException("This node isn't a HTMLElement.");
		return htmlElement;
	}

	@Override
	public IFrameName[] getAllIFrameNames(boolean needRecursion) {

		return getAllIFrameNames(getNsIDOMDocument(), needRecursion);
	}

	public IFrameName[] getAllIFrameNames(nsIDOMDocument document,
			boolean needRecursion) {
		LOG.debug("Enter getAllIFrameNames()");
		ArrayList<IFrameName> iframeList = findAllIFrameNode(document,
				needRecursion);
		LOG
				.trace("after ArrayList<IFrameName> iframeList = findAllIFrameNode(document, needRecursion);");
		SWTIFrameName[] ret = new SWTIFrameName[iframeList.size()];
		iframeList.toArray(ret);
		return ret;
	}

	@Override
	public DomNode getDocDomNode(boolean makeSureStatus) {
		return element2dom(getNsIDOMDocument());
	}

	public nsIDOMDocument getNsIDOMDocument() {
		LOG.debug("Enter getNsIDOMDocument()");
		nsIWebBrowser webBrowser = (nsIWebBrowser) browser.getWebBrowser();

		LOG
				.trace("after nsIWebBrowser webBrowser = (nsIWebBrowser) browser.getWebBrowser();");
		nsIDOMWindow window = webBrowser.getContentDOMWindow();

		LOG
				.trace("after nsIDOMWindow window = webBrowser.getContentDOMWindow();");
		nsIDOMDocument document = window.getDocument();

		LOG.debug("after nsIDOMDocument document = window.getDocument();");
		return document;
	}

	private HashMap<String, Integer> nameCountMap = new HashMap<String, Integer>();

	public ArrayList<IFrameName> findAllIFrameNode(nsIDOMDocument document,
			boolean needRecursion) {

		ArrayList<IFrameName> retList = new ArrayList<IFrameName>();
		nameCountMap.clear();
		SWTIFrameName rootFrame = SWTIFrameName.TOP_FRAME;
		rootFrame.setIframeNode(document);
		LOG.debug("Starting add IFrameNodes");
		retList.add(rootFrame);
		LOG.debug("Starting add rootFrame");

		retList.addAll(queryAllFrameNode(document, rootFrame, needRecursion));
		LOG.debug("Starting add All FrameNodes");

		retList.addAll(queryAllIFrameNode(document, rootFrame, needRecursion));
		LOG.debug("Starting add All IFrameNodes");

		return retList;
	}

	private ArrayList<IFrameName> queryAllFrameNode(nsIDOMDocument document,
			IFrameName parentFrame, boolean needRecursion) {
		if (LOG.isTraceEnabled()) {
			LOG
					.trace("Enter queryAllFrameNode(nsIDOMDocument document, IFrameName parentFrame, boolean needRecursion)");
			LOG.trace("parentFrame: " + parentFrame);
			LOG.trace("needRecursion: " + needRecursion);
		}
		ArrayList<IFrameName> retList = new ArrayList<IFrameName>();
		nsIDOMNodeList frameNodeList = document.getElementsByTagName("FRAME");
		// if (LOG.isTraceEnabled()) {
		// LOG.trace("frameNodeList = " + frameNodeList);
		//
		// if (frameNodeList != null) {
		// LOG.trace("frameNodeList.SIZE = " + frameNodeList.getLength());
		// }
		// }
		if (frameNodeList != null) {
			LOG.trace("frameNodeList is not null");
			for (int i = 0; i < frameNodeList.getLength(); ++i) {
				nsIDOMNode frameNode = frameNodeList.item(i);
				nsIDOMHTMLFrameElement frame = null;
				try {
					frame = qi(frameNode, nsIDOMHTMLFrameElement.class);
				} catch (Exception e) {
				}
				if (frame != null) {
					LOG.trace("frame is not null");
					SWTIFrameName frameName = new SWTIFrameName();
					if (frame.hasAttributes()) {
						nsIDOMNamedNodeMap attrs = frame.getAttributes();
						String id = null;
						String name = null;
						String src = null;
						for (int j = 0; j < attrs.getLength(); j++) {
							nsIDOMNode attr = attrs.item(j);
							String attrName = attr.getNodeName().toUpperCase();
							String attrValue = attr.getNodeValue();
							if ("ID".equals(attrName)) {
								id = attrValue;
							} else if ("NAME".equals(attrName)) {
								name = attrValue;
							} else if ("SRC".equals(attrName)) {
								src = attrValue;
							}

						}

						if (!StringUtil.isEmpty(id)) {
							frameName.setFrameName(id);
						} else if (!StringUtil.isEmpty(name)) {
							frameName.setFrameName(name);
						} else if (!StringUtil.isEmpty(src)) {
							frameName.setFrameName(src);
						}
						if (LOG.isTraceEnabled()) {
							LOG
									.trace("frame.NAME: "
											+ frameName.getFrameName());
						}
						if (nameCountMap.containsKey(frameName.getFrameName())) {
							int nameCount = nameCountMap.get(
									frameName.getFrameName()).intValue() + 1;
							frameName.setIndex(nameCount);
							nameCountMap.put(frameName.getFrameName(),
									new Integer(nameCount));
						} else {
							frameName.setIndex(0);
							nameCountMap.put(frameName.getFrameName(),
									new Integer(0));
						}
					}
					frameName.setIframeNode(frame.getContentDocument());
					frameName.setParentFrameName(parentFrame);
					retList.add(frameName);
					if (needRecursion)
						retList
								.addAll(queryAllFrameNode(frame
										.getContentDocument(), frameName,
										needRecursion));
				}
			}
		}
		return retList;
	}

	private ArrayList<IFrameName> queryAllIFrameNode(nsIDOMDocument document,
			IFrameName parentFrame, boolean needRecursion) {
		if (LOG.isTraceEnabled()) {
			LOG
					.trace("Enter queryAllIFrameNode(nsIDOMDocument document, IFrameName parentFrame, boolean needRecursion)");
			LOG.trace("parentFrame: " + parentFrame);
			LOG.trace("needRecursion: " + needRecursion);
		}
		ArrayList<IFrameName> retList = new ArrayList<IFrameName>();
		nsIDOMNodeList iframeNodeList = document.getElementsByTagName("IFRAME");
		// if (LOG.isTraceEnabled()) {
		// LOG.trace("IframeNodeList = " + iframeNodeList);
		//
		// if (iframeNodeList != null) {
		// LOG.trace("IframeNodeList.SIZE = " + iframeNodeList.getLength());
		// }
		// }
		if (iframeNodeList != null) {
			LOG.trace("iframeNodeList is not null");
			for (int i = 0; i < iframeNodeList.getLength(); ++i) {
				nsIDOMNode iframeNode = iframeNodeList.item(i);
				nsIDOMHTMLIFrameElement iframe = null;
				try {
					iframe = qi(iframeNode, nsIDOMHTMLIFrameElement.class);
				} catch (Exception e) {
				}
				if (iframe != null) {
					LOG.trace("frame is not null");
					SWTIFrameName frameName = new SWTIFrameName();
					if (iframe.hasAttributes()) {
						nsIDOMNamedNodeMap attrs = iframe.getAttributes();
						String id = null;
						String name = null;
						String src = null;
						for (int j = 0; j < attrs.getLength(); j++) {
							nsIDOMNode attr = attrs.item(j);
							String attrName = attr.getNodeName().toUpperCase();
							String attrValue = attr.getNodeValue();
							if ("ID".equals(attrName)) {
								id = attrValue;
							} else if ("NAME".equals(attrName)) {
								name = attrValue;
							} else if ("SRC".equals(attrName)) {
								src = attrValue;
							}

						}
						if (!StringUtil.isEmpty(id)) {
							frameName.setFrameName(id);
						} else if (!StringUtil.isEmpty(name)) {
							frameName.setFrameName(name);
						} else if (!StringUtil.isEmpty(src)) {
							frameName.setFrameName(src);
						}
						if (LOG.isTraceEnabled()) {
							LOG
									.trace("frame.NAME: "
											+ frameName.getFrameName());
						}
						if (nameCountMap.containsKey(frameName.getFrameName())) {
							int nameCount = nameCountMap.get(
									frameName.getFrameName()).intValue() + 1;
							frameName.setIndex(nameCount);
							nameCountMap.put(frameName.getFrameName(),
									new Integer(nameCount));
						} else {
							frameName.setIndex(0);
							nameCountMap.put(frameName.getFrameName(),
									new Integer(0));
						}
					}

					frameName.setIframeNode(iframe.getContentDocument());
					frameName.setParentFrameName(parentFrame);
					retList.add(frameName);
					if (needRecursion)
						retList
								.addAll(queryAllIFrameNode(iframe
										.getContentDocument(), frameName,
										needRecursion));
				}
			}
		}
		return retList;
	}

	// private static DomNode findIFrameNode(nsIDOMNode node, String frameName,
	// int index) {
	// ArrayList<nsIDOMHTMLIFrameElement> iframes = findIFrameNode(node,
	// frameName);
	// if (index < 0 || index >= iframes.size())
	// return null;
	// return element2dom(iframes.get(index).getContentDocument());
	// }

	// private static DomNode findFrameNode(nsIDOMNode node, String frameName,
	// int index) {
	// ArrayList<nsIDOMHTMLFrameElement> frames = findFrameNode(node,
	// frameName);
	// if (index < 0 || index >= frames.size())
	// return null;
	// return element2dom(frames.get(index).getContentDocument());
	// }

	// private static ArrayList<nsIDOMHTMLFrameElement> findFrameNode(
	// nsIDOMNode node, String frameName) {
	//
	// ArrayList<nsIDOMHTMLFrameElement> frames = new
	// ArrayList<nsIDOMHTMLFrameElement>();
	// nsIDOMHTMLFrameElement frame = null;
	// try {
	// frame = qi(node, nsIDOMHTMLFrameElement.class);
	// } catch (Exception e) {
	// }
	// if (frame == null) {
	// // 不是frame节点
	// nsIDOMNodeList childs = node.getChildNodes();
	// for (int i = 0; i < childs.getLength(); i++) {
	// nsIDOMNode child = childs.item(i);
	// if (child.hasChildNodes()
	// || child.getNodeName().equalsIgnoreCase("IFRAME")) {
	// frames.addAll(findFrameNode(child, frameName));
	// }
	// }
	// } else {
	// if (frame.getId().equals(frameName)
	// || frame.getName().equals(frameName)
	// || frame.getSrc().equals(frameName)) {
	// frames.add(frame);
	// }
	// }
	//
	// return frames;
	// }
	//
	// private static ArrayList<nsIDOMHTMLIFrameElement> findIFrameNode(
	// nsIDOMNode node, String frameName) {
	//
	// ArrayList<nsIDOMHTMLIFrameElement> iframes = new
	// ArrayList<nsIDOMHTMLIFrameElement>();
	// nsIDOMHTMLIFrameElement iframe = null;
	// try {
	// iframe = qi(node, nsIDOMHTMLIFrameElement.class);
	// } catch (Exception e) {
	// }
	// if (iframe == null) {
	// // 不是iframe节点
	// nsIDOMNodeList childs = node.getChildNodes();
	// for (int i = 0; i < childs.getLength(); i++) {
	// nsIDOMNode child = childs.item(i);
	// if (child.hasChildNodes()
	// || child.getNodeName().equalsIgnoreCase("IFRAME")) {
	// iframes.addAll(findIFrameNode(child, frameName));
	// }
	// }
	// } else {
	// if (iframe.getId().equals(frameName)
	// || iframe.getName().equals(frameName)
	// || iframe.getSrc().equals(frameName)) {
	// iframes.add(iframe);
	// }
	// }
	//
	// return iframes;
	// }

	public static DomNode element2dom(nsIDOMNode node) {
		String nodeName = node.getNodeName();
		if ("#document".equals(nodeName)) {
			// 当有多个HTML节点时,需查找一个非空HTML节点
			nsIDOMNodeList childs = node.getChildNodes();
			DomNode writtedNode = null;
			for (int i = 0; i < childs.getLength(); i++) {
				nsIDOMNode child = childs.item(i);
				if (child.hasChildNodes()) {
					if (writtedNode != null)
						throw new RuntimeException(
								"#document have more than one child node that is not empty.");
					writtedNode = element2dom(child);
				}
			}
			if (writtedNode == null)
				throw new RuntimeException(
						"#document have no child node that is not empty.");
			return writtedNode;
		}

		DomNode domNode = new DomNode();
		domNode.nodeName = nodeName;
		if (node.getNodeType() == nsIDOMNode.TEXT_NODE
				|| node.getNodeType() == nsIDOMNode.CDATA_SECTION_NODE) {
			domNode.nodeName = "#text";
			domNode.setNodeValue(node.getNodeValue());
			return domNode;
		} else if (node.getNodeType() == node.COMMENT_NODE) {
			String commentText = node.getNodeValue();
			commentText = StringUtil.replaceAll(commentText, "--", "");
			domNode.setNodeValue(commentText);
			return domNode;
		}

		domNode.nodeName = nodeName.toUpperCase();
		if ("SCRIPT".equals(domNode.nodeName)) {
			domNode.setNodeValue(node.getNodeValue());
		}
		if (node.hasAttributes()) {
			nsIDOMNamedNodeMap attrs = node.getAttributes();

			domNode.attributes = new HashMap<String, String>();
			for (int i = 0; i < attrs.getLength(); i++) {
				nsIDOMNode attr = attrs.item(i);
				String attrName = attr.getNodeName().toUpperCase();
				String attrValue = attr.getNodeValue();
				domNode.attributes.put(attrName, attrValue);
			}
		}

		if (node.hasChildNodes()) {
			nsIDOMNodeList childs = node.getChildNodes();
			domNode.childNodes = new ArrayList<DomNode>();
			for (int i = 0; i < childs.getLength(); i++) {
				nsIDOMNode child = childs.item(i);
				DomNode childDom = element2dom(child);
				domNode.childNodes.add(childDom);
			}
		}

		return domNode;
	}

	@Override
	public int getLoadErrorCode() {
		LOG.debug("ENTER getLoadErrorCode");

		int status = getHttpStatus();

		if (status >= 400) {
			this.loadErrorCode = responseStatus;
		}

		if (LOG.isDebugEnabled())
			LOG
					.debug("EXIT getLoadErrorCode, and return "
							+ this.loadErrorCode);
		return this.loadErrorCode;
	}

	@Override
	public int getHttpStatus() {
		LOG.debug("Enter getHttpStatus");
		synchronized (responseValue) {
			if (this.responseStatus == 0 && responseValue.size() > 0) {
				this.responseStatus = responseValue.values().iterator().next()
						.intValue();

			}
		}
		if (this.responseStatus == 0) {
			this.responseStatus = HttpStatus.SC_OK;
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("getHttpStatus() return " + this.responseStatus);
		}
		return this.responseStatus;
	}

	@Override
	public Cookie[] getCookies() {
		nsIServiceManager serviceManager = org.mozilla.xpcom.Mozilla
				.getInstance().getServiceManager();
		nsICookieManager cookieManager = (nsICookieManager) serviceManager
				.getServiceByContractID("@mozilla.org/cookiemanager;1",
						nsICookieManager.NS_ICOOKIEMANAGER_IID);

		nsISimpleEnumerator cookieEnumerator = cookieManager.getEnumerator();
		ArrayList<Cookie> list = new ArrayList<Cookie>();
		String url = getUrl();
		while (cookieEnumerator.hasMoreElements()) {
			nsICookie ncookie = qi(cookieEnumerator.getNext(), nsICookie.class);
			String domain = ncookie.getHost();
			if (domain != null) {
				domain = domain.trim();
				if (domain.length() > 0 && URLUtil.isDomain(domain, url)) {
					Cookie jcookie = new Cookie();
					jcookie.setDomain(domain);
					jcookie.setPath(ncookie.getPath());
					jcookie.setName(ncookie.getName());
					jcookie.setValue(ncookie.getValue());
					long expiryTime = ((long) ncookie.getExpires()) * 1000;
					jcookie.setExpiryDate(new Date(expiryTime));
					list.add(jcookie);
				}
			}
		}
		Cookie[] result = new Cookie[list.size()];
		list.toArray(result);
		return result;
	}

	@Override
	public void highlightElement(NodePath node, int borderColor,
			String backupAttributeName, IFrameName frame) {
		nsIDOMNode rootNode = ((SWTIFrameName) frame).getIframeNode();

		if (borderColor == 0) {

			nsIDOMNode domNode = findHtmlDomByXmlNode(rootNode, node);
			if (domNode != null) {
				nsIDOMHTMLElement htmlElement = qi(domNode,
						nsIDOMHTMLElement.class);

				if (htmlElement != null)
					recoverStyle(htmlElement, backupAttributeName);
			}
		} else {
			String color = Integer.toHexString(0x1000000 + borderColor)
					.substring(1);

			// String tsStyle = "background-color:#" + color;
			//			
			// String tsStyle = "border:5px dotted #" + color;
			nsIDOMNode domNode = findHtmlDomByXmlNode(rootNode, node);
			if (domNode != null) {
				nsIDOMHTMLElement htmlElement = qi(domNode,
						nsIDOMHTMLElement.class);
				if (htmlElement != null) {

					String tsStyle = "";
					String nodename = htmlElement.getNodeName().toUpperCase();

					if (ArrayUtil.content(backgroundColorNodeName, nodename)) {
						tsStyle = "background-color:#" + color;
					} else {
						tsStyle = "border:5px dotted #" + color;
					}

					setStyle(htmlElement, tsStyle, backupAttributeName);
				}

				// htmlElement.setAttribute("style", tsStyle);
			}
		}
	}

	private String[] backgroundColorNodeName = new String[] { "ADDRESS", "COL",
			"COLGROUP", "currentStyle", "HTML", "IFRAME", "LEGEND", "OPTION",
			"SELECT", "TBODY", "TFOOT", "THEAD", "TR" };

	@Override
	public void highlightElements(NodePath[] nodes, int borderColor,
			String backupAttributeName, IFrameName frame) {
		nsIDOMNode rootNode = ((SWTIFrameName) frame).getIframeNode();

		/*
		 * String tsStyle; if (borderColor == 0) tsStyle = "border: none"; else
		 * { tsStyle = Integer.toHexString(0x1000000 +
		 * borderColor).substring(1); tsStyle = "border: thick dotted #" +
		 * tsStyle; } for (int i = 0; i < nodes.length; i++) { nsIDOMNode
		 * domNode = findHtmlDomByXmlNode(document, nodes[i]); if (domNode ==
		 * null) continue; nsIDOMHTMLElement htmlElement = (nsIDOMHTMLElement)
		 * domNode .queryInterface(nsIDOMHTMLElement.NS_IDOMHTMLELEMENT_IID); if
		 * (htmlElement == null) continue; htmlElement.setAttribute("style",
		 * tsStyle); }
		 */
		if (borderColor == 0) {
			for (int i = 0; i < nodes.length; i++) {
				nsIDOMNode domNode = findHtmlDomByXmlNode(rootNode, nodes[i]);
				if (domNode == null)
					continue;
				nsIDOMHTMLElement htmlElement = qi(domNode,
						nsIDOMHTMLElement.class);
				if (htmlElement != null)
					recoverStyle(htmlElement, backupAttributeName);
			}
		} else {
			String color = Integer.toHexString(0x1000000 + borderColor)
					.substring(1);
			// String tsStyle = "background-color:#" + color;//
			// "border: thick dotted #"

			// + color;
			for (int i = 0; i < nodes.length; i++) {
				nsIDOMNode domNode = findHtmlDomByXmlNode(rootNode, nodes[i]);
				if (domNode == null)
					continue;
				nsIDOMHTMLElement htmlElement = qi(domNode,
						nsIDOMHTMLElement.class);
				if (htmlElement != null) {
					String tsStyle = "";
					String nodename = htmlElement.getNodeName().toUpperCase();

					if (ArrayUtil.content(backgroundColorNodeName, nodename)) {
						tsStyle = "background-color:#" + color;
					} else {
						tsStyle = "border:5px dotted #" + color;
					}
					setStyle(htmlElement, tsStyle, backupAttributeName);
				}

				// htmlElement.setAttribute("style", tsStyle);

			}
		}

	}

	private void setStyle(nsIDOMHTMLElement element, String style,
			String backupAttributeName) {
		if (element == null)
			return;
		String nodename = element.getNodeName().toUpperCase();

		if (element.hasChildNodes()
				&& ArrayUtil.content(backgroundColorNodeName, nodename)) {
			nsIDOMNodeList nodes = element.getChildNodes();

			for (int i = 0; i < nodes.getLength(); ++i) {
				nsIDOMNode node = nodes.item(i);
				if (node.hasAttributes()) {
					nsIDOMHTMLElement childElement = qi(node,
							nsIDOMHTMLElement.class);
					if (childElement != null)
						setStyle(childElement, style, backupAttributeName);
				}
			}
		}
		// String backStyle = element.getAttribute(backupAttributeName);
		// if (backStyle == null || backStyle.trim().length() == 0) {
		element
				.setAttribute(backupAttributeName, element
						.getAttribute("style"));

		element.setAttribute("style", style);
	}

	private void recoverStyle(nsIDOMHTMLElement element,
			String backupAttributeName) {
		if (element == null)
			return;

		String backStyle = element.getAttribute(backupAttributeName);
		String nodename = element.getNodeName().toUpperCase();

		if (element.hasChildNodes()
				&& ArrayUtil.content(backgroundColorNodeName, nodename)) {
			nsIDOMNodeList nodes = element.getChildNodes();

			for (int i = 0; i < nodes.getLength(); ++i) {
				nsIDOMNode node = nodes.item(i);
				if (node.hasAttributes()) {
					nsIDOMHTMLElement childElement = qi(node,
							nsIDOMHTMLElement.class);
					if (childElement != null)
						recoverStyle(childElement, backupAttributeName);
				}
			}
		}
		element.setAttribute("style", backStyle);

	}

	nsIDOMNode findHtmlDomByXmlNode(nsIDOMNode domNode, NodePath nodePath) {
		String nodeName = domNode.getNodeName();
		if ("#document".equals(nodeName)) {
			// 当有多个HTML节点时,需查找一个非空HTML节点
			nsIDOMNodeList childs = domNode.getChildNodes();
			nsIDOMNode writtedNode = null;
			for (int i = 0; i < childs.getLength(); i++) {
				nsIDOMNode child = childs.item(i);
				if (child.hasChildNodes()) {
					if (writtedNode != null)
						throw new RuntimeException(
								"#document have more than one child node that is not empty.");
					writtedNode = findHtmlDomByXmlNode(child, nodePath);
				}
			}
			return writtedNode;
		}

		if (!nodeName.equalsIgnoreCase(nodePath.nodeName(0)))
			return null;
		NodePath nextPath = nodePath.nextPath();
		if (nextPath == null) // 查找完成
			return domNode;

		nsIDOMNodeList childs = domNode.getChildNodes();
		int nextPathNodeIndex = nextPath.nodeSameTagIdx(0);
		if (childs.getLength() <= nextPathNodeIndex)
			return null;
		String nextPathNodeName = nextPath.nodeName(0);
		int index = -1;
		for (int i = 0; i < childs.getLength(); i++) {
			nsIDOMNode child = childs.item(i);
			String childName = child.getNodeName();
			if (nextPathNodeName.equalsIgnoreCase(childName)) {
				if (nextPathNodeIndex == -1)
					return findHtmlDomByXmlNode(child, nextPath);

				index++;
				if (index == nextPathNodeIndex)
					return findHtmlDomByXmlNode(child, nextPath);
			}
		}
		return null;
	}

	@Override
	public void initForRobotEditor(RobotEditorListener robotEditorListener) {

		// IFrame必须禁止.否则IFrame可能有changing无httpmointion/changed相关事件,导致加载状态失败
		// this.setIFrameEnabled(false);

		this.setFlashEnabled(false);
		this.setImageEnabled(false);
		this.setJavascriptEnabled(true);

		this.robotEditorListener = robotEditorListener;
		this.skipRobotEditorListener = (robotEditorListener == null);

		registerPromptService(new MozillaPromptService2());
	}

	/**
	 * 注册弹出提示框的自动处理
	 * 
	 * @param promptService
	 */
	void registerPromptService(final nsIPromptService2 promptService) {
		nsIComponentRegistrar registrar = org.mozilla.xpcom.Mozilla
				.getInstance().getComponentRegistrar();
		final String NS_PROMPTSERVICE_CID = "a2112d6a-0e28-421f-b46a-25c0b308cbd0";
		registrar.registerFactory(NS_PROMPTSERVICE_CID, "Prompt Service",
				XPCOM.NS_PROMPTSERVICE_CONTRACTID, new nsIFactory() {
					public nsISupports queryInterface(String uuid) {
						if (uuid.equals(nsIFactory.NS_IFACTORY_IID)
								|| uuid.equals(nsIFactory.NS_ISUPPORTS_IID))
							return this;
						return null;
					}

					public nsISupports createInstance(nsISupports outer,
							String iid) {
						return promptService;
					}

					public void lockFactory(boolean lock) {
					}
				});
	}

	private static final String REQUEST = "http-on-modify-request";
	private static final String RESPONSE = "http-on-examine-response";
	private static final String CACHE_RESPONSE = "http-on-examine-cached-response";
	private AtomicBoolean isloadCurrentURLComplete = new AtomicBoolean(false);

	public void setUserAgent(String s) {
		nsIPrefService service = (nsIPrefService) getService(
				"@mozilla.org/preferences-service;1",
				org.mozilla.interfaces.nsIPrefService.class);

		nsIPrefBranch branch = (nsIPrefBranch) service
				.getBranch("general.useragent.");

		if (s == null || s.length() == 0) {
			if (branch.getPrefType("override") != nsIPrefBranch.PREF_INVALID)
				branch.clearUserPref("override");
			return;
		} else {
			branch.setCharPref("override", s);
			return;
		}
	}

	/**
	 * 注册http监测器
	 */
	@Override
	protected void registerHttpMonitor() {
		nsIObserver observer = new nsIObserver() {

			public nsISupports queryInterface(String uuid) {
				if (uuid.equals(nsIObserver.NS_IOBSERVER_IID)
						|| uuid.equals(nsISupports.NS_ISUPPORTS_IID))
					return this;
				return null;
			}

			public void observe(nsISupports subject, String topic, String data) {
				nsIHttpChannel httpChannel = (nsIHttpChannel) subject
						.queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID);
				String httpUrl = httpChannel.getName();

				if (RESPONSE.equals(topic) || CACHE_RESPONSE.equals(topic)) {
					// if (theResponseStatus >= 300 && theResponseStatus < 400)
					// {
					// resetRequestMap();
					// }
					removeRequest(httpUrl);
					if (httpUrl.equals(loadingUrl)) {
						long theResponseStatus = httpChannel
								.getResponseStatus();
						if (theResponseStatus > 0) {
							synchronized (responseValue) {
								responseValue.put(httpUrl, theResponseStatus);
							}
							if (theResponseStatus == HttpStatus.SC_OK
									|| (theResponseStatus > 300 && theResponseStatus < 400)) {

								isloadCurrentURLComplete.set(true);
							}
						}

					}

				} else if (REQUEST.equals(topic)) {

					addRequest(httpUrl);
				} else {
					if (LOG.isTraceEnabled())
						LOG.trace(topic);
				}
			}
		};
		nsIServiceManager serviceManager = org.mozilla.xpcom.Mozilla
				.getInstance().getServiceManager();

		nsIObserverService observerService = (nsIObserverService) serviceManager
				.getServiceByContractID(XPCOM.NS_OBSERVER_CONTRACTID,
						nsIObserverService.NS_IOBSERVERSERVICE_IID);

		observerService.addObserver(observer, REQUEST, false);
		observerService.addObserver(observer, CACHE_RESPONSE, false);
		observerService.addObserver(observer, RESPONSE, false);
	}

	private LocationListener locationListener;

	private OpenWindowListener openWindowListener;

	private ProgressListener progressListener;

	// final nsIDOMEventListener mouseDownListener = new

	// final nsIDOMEventListener disableMouseRightClickListener = new
	// nsIDOMEventListener() {
	//
	// @Override
	// public void handleEvent(nsIDOMEvent event) {
	// System.out.println("MouseClick：" + event);
	// nsIDOMMouseEvent mouseEvent = (nsIDOMMouseEvent) event
	// .queryInterface(nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
	// int mouseButton = mouseEvent.getButton();
	// if (mouseButton == MouseEvent.BUTTON2 /* 鼠标右键 */) {
	//
	// event.preventDefault();
	// event.stopPropagation();
	// }
	// }
	//
	// @Override
	// public nsISupports queryInterface(String uuid) {
	// return uuid.equals(nsIDOMEventListener.NS_IDOMEVENTLISTENER_IID) ? this
	// : null;
	// }
	//
	// };

	private class DOMMouseEventListener implements nsIDOMEventListener {
		private DomNode rootNode;

		private IFrameName getIFrameName(nsIDOMDocument thisDocument) {
			IFrameName[] frameNames = getAllIFrameNames(true);
			for (int i = 0; i < frameNames.length; ++i) {
				if (frameNames[i] instanceof SWTIFrameName
						&& ((SWTIFrameName) frameNames[i]).getIframeNode()
								.equals(thisDocument))
					return frameNames[i];
			}
			return null;
		}

		public void handleEvent(nsIDOMEvent event) {
			if (skipRobotEditorListener)
				return;
			nsIDOMMouseEvent mouseEvent = (nsIDOMMouseEvent) event
					.queryInterface(nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
			int mouseButton = mouseEvent.getButton();
			if (mouseButton == MouseEvent.BUTTON2 /* 鼠标右键 */) {
				// if (LOG.isTraceEnabled()) {
				// LOG.trace("Mouse Right Button Click in Browser. ClickCancelable: "
				// + mouseEvent.getCancelable()
				// + "; Type: "
				// + mouseEvent.getType());
				// }
				event.preventDefault();
				event.stopPropagation();

				boolean isJavascriptEnabled = enableJavaScript;
				setJavascriptEnabled(false);
				nsIDOMEventTarget target = mouseEvent.getTarget();

				nsIDOMHTMLElement element = qi(target, nsIDOMHTMLElement.class);
				nsIDOMDocument document = element.getOwnerDocument();

				if (rootNode == null) {
					rootNode = element2dom(document);
				}
				NodePath nodePath = NodePath.domNodePath(element);

				IFrameName frameName = getIFrameName(document);

				// robotEditorListener.nodeClick(nodePath);
				// TODO syncExec
				// SwingUtilities.invokeLater(new Runnable() {
				// public void run() {
				if (frameName != null) {
					robotEditorListener
							.nodeClick(rootNode, nodePath, frameName);
				}

				setJavascriptEnabled(isJavascriptEnabled);
				// }
				// });
			}
		}

		public nsISupports queryInterface(String uuid) {
			return uuid.equals(nsIDOMEventListener.NS_IDOMEVENTLISTENER_IID) ? this
					: null;
		}
	};

	@Override
	public Image readImageData(String imgElementId) {
		final String script = "image = document.getElementById('"
				+ imgElementId + "'); "
				+ "\n canvas = document.createElement('canvas');"
				+ "\n canvas.width = image.width;"
				+ "\n canvas.height = image.height;"
				+ "\n ctx = canvas.getContext('2d');"
				+ "\n ctx.drawImage(image, 0,0);"
				+ "\n data = canvas.toDataURL('image/png');"
				+ "\n return data;";
		if (LOG.isTraceEnabled())
			LOG.trace("Script: " + script);

		String dataString = (String) browser.evaluate(script);
		String strData = dataString
				.substring("data:image/png;base64,".length());
		try {
			byte[] binData = Base64.decodeBase64(strData);
			Image image = ImageIO.read(new ByteArrayInputStream(binData));
			return image;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getBrowserTypeValue() {
		return "SWT.MOZILLA";
	}

	@Override
	public boolean back() {
		allRequestFinished.set(false);
		synchronized (latch) {
			latch = new CountDownLatch(1);
		}
		return browser.back();
	}

	@Override
	public boolean forward() {

		allRequestFinished.set(false);
		synchronized (latch) {
			latch = new CountDownLatch(1);
		}
		return browser.forward();
	}

	@Override
	public void refresh() {
		// loading = true;
		allRequestFinished.set(false);
		synchronized (latch) {
			latch = new CountDownLatch(1);
		}
		browser.refresh();
	}

	@Override
	public void stop() {

		synchronized (latch) {
			latch.countDown();
		}
		browser.stop();
		allRequestFinished.set(true);
	}

	@Override
	public void dispose() {
		browser.dispose();
	}

	@Override
	public void execute(String script) {
		executeJavaScript(script, null);
	}

	private void executeJavaScript(String script, nsIDOMHTMLElement htmlElement) {
		script = formatScript(script, htmlElement);
		browser.execute(script);
	}

	@Override
	public String getHtmlSourceCode() {
		return browser.getText();
	}

	@Override
	public String getUrl() {
		return browser.getUrl();
	}

	@Override
	public void invokeMethod(final Object methodObj, String methodName) {
		Class ownerClass = methodObj.getClass();
		Class[] argsClass = new Class[] { Object[].class };

		try {
			final Method method = ownerClass.getMethod(methodName, argsClass);
			new BrowserFunction(browser, methodName) {
				public Object function(Object[] arguments) {
					try {
						return method.invoke(methodObj,
								new Object[] { arguments });
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} catch (InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			};
		} catch (SecurityException e1) {
			throw new RuntimeException(e1);
		} catch (NoSuchMethodException e1) {
			throw new RuntimeException(e1);
		}

	}

	private nsIPrefBranch pref;
	private nsIDownloadManager downloadManager;

	@Override
	public void setHtmlSourceCode(String html) {
		browser.setText(html);
	}

	@Override
	public void setUrl(String url) {
		browser.setUrl(url);
	}

	private HashMap<String, Long> responseValue = new HashMap<String, Long>();

	@Override
	protected LocationListener getLocationListener() {
		if (locationListener == null) {
			locationListener = new LocationListener() {
				/**
				 * 经测试，当加载一个页面时，正常的顺序依次为 一个 LocationListener.changing 一个
				 * LocationListener.changed 多个 ProgressListener.changed 一个
				 * ProgressListener.completed
				 * 
				 * 当LocationListener.changing设置event.doit = false时,
				 * 将只有LocationListener.changing无其它事件发生
				 */
				public void changing(LocationEvent event) {
					if (LOG.isDebugEnabled())
						LOG.debug("LocationListener.changing " + event.location);
					// // if (skipRobotEditorListener) {
					// loading = true;

					// }
					allRequestFinished.set(false);
					resetRequestMap();
					if (latch != null) {
						while (latch.getCount() > 0) {
							latch.countDown();
						}
					}
					latch = new CountDownLatch(1);

					// synchronized (cookieList) {
					for (int i = 0; i < cookieList.size(); ++i) {
						Browser.setCookie(cookieList.get(i), event.location);
					}
					/*
					 * if (LOG.isDebugEnabled()) LOG.debug("begin url: " + url);
					 * // browser.setUrl(url); if (LOG.isDebugEnabled())
					 * LOG.debug("end url: " + url);
					 */
					cookieList.clear();
					// System.out.println("browser.url.changing");
					isloadCurrentURLComplete.set(false);
					loadingUrl = event.location;
					responseStatus = 0;
					loadErrorCode = 0;
					synchronized (responseValue) {
						responseValue.clear();
						responseValue.put(loadingUrl, (long) 0);
					}
					// // httpMonitorObserved = false;
					//
					// if (!event.top && robotEditorListener != null) {
					// SwingUtilities.invokeLater(new Runnable() {
					// public void run() {
					// // ensureLoadComplete();
					// if (LOG.isDebugEnabled()) {
					// LOG.debug("load iframe " + loadingUrl + " done.");
					// }
					// robotEditorListener.locationChanged();
					// }
					// });
					//
					// }

				}

				public void changed(LocationEvent event) {
					if (LOG.isDebugEnabled())
						LOG.debug("LocationListener.changed " + event.location);
					// responseUrl = event.location;
					// if (skipRobotEditorListener) {

					// loading = true;
					// if(latch.getCount()==0){
					// latch = new CountDownLatch(1);
					// }

					loadingUrl = event.location;
					responseStatus = 0;
					loadErrorCode = 0;
					// httpMonitorObserved = false;

					if (!event.top && robotEditorListener != null) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								// ensureLoadComplete();
								if (LOG.isDebugEnabled()) {
									LOG.debug("load iframe " + loadingUrl
											+ " done.");
								}
								robotEditorListener.locationChanged();
							}
						});
					}
					Runtime.getRuntime().gc();
				}

			};
		}
		return locationListener;
	}

	@Override
	protected OpenWindowListener getOpenWindowListener() {
		if (openWindowListener == null) {
			openWindowListener = new OpenWindowListener() {
				public void open(WindowEvent event) {

					// 在本窗口直接打开需要新窗口的页面
					event.browser = SWTMozilla.this.browser;
				}
			};
		}
		return openWindowListener;
	}

	@Override
	protected ProgressListener getProgressListener() {
		if (progressListener == null) {
			progressListener = new ProgressListener() {
				public void changed(ProgressEvent event) {
					if (LOG.isDebugEnabled())
						LOG.debug("ProgressListener.changed " + event);

					// LOG.info("ProgressListener.changed " + event);
					// nsIDOMDocument document = getNsIDOMDocument();
					// nsIDOMDocumentView documentView = qi(document,
					// nsIDOMDocumentView.class);
					// nsIDOMAbstractView abstractView =
					// documentView.getDefaultView();
					//
					// nsIInterfaceRequestor requestor = qi(abstractView,
					// nsIInterfaceRequestor.class);
					// nsIWebNavigation navigation = (nsIWebNavigation)
					// requestor
					// .getInterface(nsIWebNavigation.NS_IWEBNAVIGATION_IID);
					//
					// nsIDocShell docShell = qi(navigation, nsIDocShell.class);
					//
					// // nsIDocShell docShell = qi(window, nsIDocShell.class);
					// docShell.setUseErrorPages(true);
				}

				public void completed(ProgressEvent event) {

					LOG.debug("ProgressListener.completed.");
					if (latch != null) {
						latch.countDown();
					}
					executeOnLoadComplete();
					// if (allRequestFinished()) {
					// if (LOG.isDebugEnabled())
					// LOG.debug("allRequestFinished() == true");
					//
					// allRequestFinished.set(true);
					//
					// if (LOG.isDebugEnabled())
					// LOG.debug("before latch.getCount() = " +
					// latch.getCount());
					// latch.countDown();
					// if (LOG.isDebugEnabled())
					// LOG.debug("after latch.getCount() = " +
					// latch.getCount());
					//
					// LOG.debug("Exit ProgressListener.completed.");
					// }

				}
			};
		}
		return progressListener;
	}

	private void executeOnLoadComplete() {

		if (robotEditorListener != null
				&& SystemInterface.MODE_EDIT
						.equals(((SystemInterface) ApplicationContext
								.getInstance().getBean("systemInterface"))
								.getMode())) {

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// ensureLoadComplete();

					if (LOG.isDebugEnabled()) {
						LOG
								.debug("Loading complete before robotEditorListener.locationChanged().");
					}
					robotEditorListener.locationChanged();
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							nsIDOMDocument document = getNsIDOMDocument();

							nsIDOMNodeList iframeNodes = document
									.getElementsByTagName("IFRAME");

							nsIDOMNodeList frameNodes = document
									.getElementsByTagName("FRAME");

							nsIDOMEventTarget target = qi(document,
									nsIDOMEventTarget.class);
							target.addEventListener("mousedown",
									new DOMMouseEventListener(), false);
							// target.addEventListener("click",
							// disableMouseRightClickListener,
							// false);

							if (iframeNodes != null) {
								HashMap<String, Integer> nameCountMap = new HashMap<String, Integer>();
								if (LOG.isTraceEnabled()) {
									LOG.trace("Number of IFrame: "
											+ iframeNodes.getLength());
								}
								for (int i = 0; i < iframeNodes.getLength(); ++i) {
									nsIDOMNode iframeNode = iframeNodes.item(i);
									nsIDOMHTMLIFrameElement iframe = qi(
											iframeNode,
											nsIDOMHTMLIFrameElement.class);
									SWTIFrameName frame = new SWTIFrameName();
									if (!StringUtil.isEmpty(iframe.getId())) {
										frame.setFrameName(iframe.getId());
									} else if (!StringUtil.isEmpty(iframe
											.getName())) {

										frame.setFrameName(iframe.getName());
									} else if (!StringUtil.isEmpty(iframe
											.getSrc())) {
										frame.setFrameName(iframe.getSrc());
									} else if (!StringUtil.isEmpty(iframe
											.getTitle())) {
										frame.setFrameName(iframe.getTitle());
									}

									if (nameCountMap.containsKey(frame
											.getFrameName())) {
										int nameCount = nameCountMap.get(
												frame.getFrameName())
												.intValue() + 1;
										frame.setIndex(nameCount);

										nameCountMap.put(frame.getFrameName(),
												new Integer(nameCount));
									} else {
										nameCountMap.put(frame.getFrameName(),
												new Integer(0));
										frame.setIndex(0);
									}
									frame.setIframeNode(iframeNode);
									target = qi(iframe.getContentDocument(),
											nsIDOMEventTarget.class);
									DOMMouseEventListener mouseDownListener = new DOMMouseEventListener();

									target.addEventListener("mousedown",
											mouseDownListener, false);
									//
									// target.addEventListener("click",
									// disableMouseRightClickListener,
									// false);

								}
							}

							if (frameNodes != null) {
								HashMap<String, Integer> nameCountMap = new HashMap<String, Integer>();
								if (LOG.isTraceEnabled()) {
									LOG.trace("Number of IFrame: "
											+ frameNodes.getLength());
								}
								for (int i = 0; i < frameNodes.getLength(); ++i) {
									nsIDOMNode frameNode = frameNodes.item(i);
									nsIDOMHTMLFrameElement iframe = qi(
											frameNode,
											nsIDOMHTMLFrameElement.class);
									SWTIFrameName frame = new SWTIFrameName();
									if (!StringUtil.isEmpty(iframe.getId())) {
										frame.setFrameName(iframe.getId());
									} else if (!StringUtil.isEmpty(iframe
											.getName())) {

										frame.setFrameName(iframe.getName());
									} else if (!StringUtil.isEmpty(iframe
											.getSrc())) {
										frame.setFrameName(iframe.getSrc());
									} else if (!StringUtil.isEmpty(iframe
											.getTitle())) {
										frame.setFrameName(iframe.getTitle());
									}

									if (nameCountMap.containsKey(frame
											.getFrameName())) {
										int nameCount = nameCountMap.get(
												frame.getFrameName())
												.intValue() + 1;
										frame.setIndex(nameCount);

										nameCountMap.put(frame.getFrameName(),
												new Integer(nameCount));
									} else {
										nameCountMap.put(frame.getFrameName(),
												new Integer(0));
										frame.setIndex(0);
									}
									frame.setIframeNode(frameNode);
									target = qi(iframe.getContentDocument(),
											nsIDOMEventTarget.class);
									DOMMouseEventListener mouseDownListener = new DOMMouseEventListener();

									target.addEventListener("mousedown",
											mouseDownListener, false);
									//
									// target.addEventListener("click",
									// disableMouseRightClickListener,
									// false);
								}
							}
						}
					};
					UIOperation operation = new UIOperation("", currentBrowser,
							runnable);
					operation.syncExec();
				}
			});
		}
	}

	@Override
	protected int getBrowserType() {
		return SWT.MOZILLA;
	}

	@Override
	public void changeCharset(String charset) {
		// TODO changeCharset

	}

	@Override
	public BrowserDocument getDocument() {
		final BrowserDocument[] document = new BrowserDocument[1];

		if (LOG.isDebugEnabled())
			LOG.debug("create SWTBrowserDocument");
		document[0] = new SWTBrowserDocument(getNsIDOMDocument());
		IFrameName[] frameNames = getAllIFrameNames(false);

		if (LOG.isDebugEnabled())
			LOG.debug("Finished getAllIframeNames in getDocument.");
		if (frameNames != null) {
			String[] frameNameString = new String[frameNames.length];
			for (int i = 0; i < frameNames.length; ++i) {
				frameNameString[i] = frameNames[i].getFrameName();
			}
			document[0].setFrameNames(frameNameString);
		}

		return document[0];
	}

	@Override
	protected void configBrowser() {
		LOG.debug("Enter SWTMozilla.configBrowser()");
		if (pref == null)
			pref = getService("@mozilla.org/preferences-service;1",
					nsIPrefBranch.class);

		// disable download manager
		pref.setBoolPref("browser.download.manager.alertOnEXEOpen", 0);
		pref.setBoolPref("browser.download.manager.closeWhenDone", 1);
		pref.setBoolPref("browser.download.manager.closeWhenDone", 1);
		pref.setBoolPref("browser.download.manager.showAlertOnComplete", 0);
		pref.setBoolPref("browser.download.manager.showWhenStarting", 0);

		// disable popup window
		pref.setBoolPref("ui.use_native_popup_windows", 0);
		pref.setCharPref("dom.popup_allowed_events", "mouseup click");
		pref.setBoolPref("dom.disable_open_during_load", 1);
		pref.setBoolPref("browser.urlbar.showPopup", 0);
		pref.setIntPref("browser.link.open_newwindow.restriction", 2);

		// Cache in memory Size
		pref.setBoolPref("browser.cache.memory.enable", 1);
		pref.setIntPref("browser.cache.memory.capacity", 51200);
		// Cache in disk Size
		pref.setBoolPref("browser.cache.disk.enable", 1);
		pref.setIntPref("browser.cache.disk.capacity", 51200);

		// Set it to false. Network link prefetch will download all pages with
		// the rel=”prefetch” tag, with false you avoid that downloading.
		pref.setBoolPref("network.prefetch-next", 0);
		// Set it to true. This mean – when you minimize the firefox window it
		// will free up memory.
		pref.setBoolPref("config.trim_on_minimize", 1);
		// Set it to low number. I set it to 6. This controls how many pages of
		// history are kept in the back/forward buttons
		pref.setIntPref("browser.sessionhistory.max_total_viewers", 1);
		// pref.setIntPref("browser.sessionhistory.max_total_viewers", 6);

		String userAgentString = "Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko/20100101 Firefox/11.0";
		if (Config.isLinux()) {
			userAgentString = "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.17) Gecko/20110422 Ubuntu/10.04 (lucid) Firefox/3.6.17";
		}
		setUserAgent(userAgentString);
		LOG.debug("Exit SWTMozilla.configBrowser()");
	}

	@Override
	protected void setAlertDialogs(boolean enabled) {
		if (pref == null)
			pref = getService("@mozilla.org/preferences-service;1",
					nsIPrefBranch.class);

		pref.setBoolPref("browser.xul.error_pages.enabled", enabled ? 0 : 1);
		pref.setIntPref("alerts.totalOpenTime", 500);

		// disable various security warning dialogs
		// such as when entering/leaving https site or
		// submitting form
		pref.setBoolPref("security.warn_entering_secure", enabled ? 1 : 0);
		pref.setBoolPref("security.warn_entering_weak", enabled ? 1 : 0);
		pref.setBoolPref("security.warn_leaving_secure", enabled ? 1 : 0);
		pref.setBoolPref("security.warn_submit_insecure", enabled ? 1 : 0);
		pref.setBoolPref("security.warn_viewing_mixed", enabled ? 1 : 0);
		pref.setBoolPref("security.xpconnect.plugin.unrestricted", enabled ? 1
				: 0);

		pref.setBoolPref("plugin.override_internal_types", enabled ? 1 : 0);

		pref.setBoolPref("xpinstall.enabled", enabled ? 1 : 0);

		// disable message box on plug-ins installing
		pref.setBoolPref("plugin.default_plugin_disabled", enabled ? 0 : 1);

		// disable messagebox of downloading completed
		pref.setBoolPref("browser.download.manager.showalertOnComplete",
				enabled ? 1 : 0);

	}

	@Override
	public void clearSessions() {
		Browser.clearSessions();

		nsIServiceManager serviceManager = org.mozilla.xpcom.Mozilla
				.getInstance().getServiceManager();

		nsICookieManager manager = (nsICookieManager) serviceManager
				.getServiceByContractID(XPCOM.NS_COOKIEMANAGER_CONTRACTID,
						nsICookieManager.NS_ICOOKIEMANAGER_IID);

		manager.removeAll();
	}

}

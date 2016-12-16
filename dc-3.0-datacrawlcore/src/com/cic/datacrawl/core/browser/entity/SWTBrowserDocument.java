package com.cic.datacrawl.core.browser.entity;

import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMHTMLFrameElement;
import org.mozilla.interfaces.nsIDOMHTMLIFrameElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

import com.cic.datacrawl.core.browser.SWTMozilla;

public class SWTBrowserDocument extends BrowserDocument {
	private nsIDOMDocument domDocument;

	public SWTBrowserDocument(nsIDOMDocument domDocument) {
		super();
		this.domDocument = domDocument;
	}

	@Override
	protected IFrameElement createFrameElement(final String name) {
		if (domDocument == null) {
			return null;
		}
		final IFrameElement[] ret = new IFrameElement[1];
		// getBrowser().syncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		ret[0] = findFrameElement(domDocument, name);
		// }
		// });
		if (ret[0] != null) {
			ret[0].setBrowser(getBrowser());
			ret[0].setContentReplacementMap(getContentReplacementMap());
		}
		return ret[0];
	}

	private IFrameElement findFrameNode(nsIDOMDocument document, String name) {
		nsIDOMNodeList frameNodeList = document.getElementsByTagName("FRAME");

		if (frameNodeList != null) {
			for (int i = 0; i < frameNodeList.getLength(); ++i) {
				nsIDOMNode frameNode = frameNodeList.item(i);
				nsIDOMHTMLFrameElement frame = null;
				try {
					frame = SWTMozilla.qi(frameNode, nsIDOMHTMLFrameElement.class);
				} catch (Exception e) {
				}
				if (frame != null
					&& (name.equals(frame.getId())
						|| name.equals(frame.getName())
						|| name.equals(frame.getSrc()) || name.equals(frame.getTitle()))) {

					return new SWTMozillaIFrameElement(frame.getContentDocument());

				}
			}
		}
		return null;
	}

	private IFrameElement findIFrameNode(nsIDOMDocument document, String name) {
		nsIDOMNodeList frameNodeList = document.getElementsByTagName("IFRAME");

		if (frameNodeList != null) {
			for (int i = 0; i < frameNodeList.getLength(); ++i) {
				nsIDOMNode frameNode = frameNodeList.item(i);
				nsIDOMHTMLIFrameElement iframe = null;
				try {
					iframe = SWTMozilla.qi(frameNode, nsIDOMHTMLIFrameElement.class);
				} catch (Exception e) {
				}

				if (iframe != null
					&& (name.equals(iframe.getId())
						|| name.equals(iframe.getName())
						|| name.equals(iframe.getSrc()) || name.equals(iframe.getTitle()))) {

					return new SWTMozillaIFrameElement(iframe.getContentDocument());

				}
			}
		}
		return null;
	}

	private IFrameElement findFrameElement(nsIDOMDocument document, String name) {

		IFrameElement ret = findFrameNode(document, name);
		if (ret == null)
			ret = findIFrameNode(document, name);

		return ret;
	}

	private String htmlContent;
	private String xmlContent;

	@Override
	public String getHtmlContent() {
		if (htmlContent == null) {
			SWTMozilla mozilla = SWTMozilla.getDefaultInstance();
			if (domDocument.equals(mozilla.getNsIDOMDocument())) {
				mozilla.syncExec(new Runnable() {

					@Override
					public void run() {
						htmlContent = getBrowser().getHtmlSourceCode();
					}
				});
			} else {
				mozilla.syncExec(new Runnable() {

					@Override
					public void run() {
						htmlContent = SWTMozilla.element2dom(domDocument).toString();
						xmlContent = filterString(htmlContent);
					}
				});
			}
		}
		return htmlContent;
	}

	@Override
	public String getXmlContent() {

		if (xmlContent == null) {
			SWTMozilla.getDefaultInstance().syncExec(new Runnable() {
				@Override
				public void run() {
					xmlContent = filterString(SWTMozilla.element2dom(domDocument).toString());
				}
			});
		}

		return xmlContent;
	}

}

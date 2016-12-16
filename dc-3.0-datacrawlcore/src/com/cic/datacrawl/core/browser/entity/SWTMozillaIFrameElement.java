package com.cic.datacrawl.core.browser.entity;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.mozilla.interfaces.nsIDOMDocument;

import com.cic.datacrawl.core.browser.SWTMozilla;

public class SWTMozillaIFrameElement extends IFrameElement {
	private static final Logger LOG = Logger.getLogger(SWTMozillaIFrameElement.class);

	private nsIDOMDocument document;

	public SWTMozillaIFrameElement(nsIDOMDocument contentDocument) {
		super();
		this.document = contentDocument;
	}

	@Override
	protected BrowserDocument initIFrameDocument() {
		final SWTBrowserDocument browserDocument = new SWTBrowserDocument(document);
		SWTMozilla.getDefaultInstance().syncExec(new Runnable() {

			@Override
			public void run() {
				if (LOG.isDebugEnabled()){
					LOG.debug("Starting initialize SWTBrowserDocument");
				}
				IFrameName[] frameNames = SWTMozilla.getDefaultInstance().getAllIFrameNames(document, false);
				
				if (frameNames != null) {
					ArrayList<String> nameList = new ArrayList<String>();
					for (int i = 0; i < frameNames.length; ++i) {
						if (!IFrameName.MAIN_FRAME_NAME.equals(frameNames[i].getFrameName())) {
							nameList.add(frameNames[i].getFrameName());
							if (LOG.isTraceEnabled())
								LOG.trace(frameNames[i].getFrameName());
						}
					}
					String[] frameNameString = new String[nameList.size()];
					nameList.toArray(frameNameString);
					browserDocument.setFrameNames(frameNameString);
				}
			}
		});

		return browserDocument;
	}

}

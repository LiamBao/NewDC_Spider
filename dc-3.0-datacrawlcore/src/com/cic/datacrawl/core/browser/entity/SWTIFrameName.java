package com.cic.datacrawl.core.browser.entity;

import org.mozilla.interfaces.nsIDOMNode;

import com.cic.datacrawl.core.browser.SWTMozilla;

public class SWTIFrameName extends IFrameName {
	public static final SWTIFrameName TOP_FRAME = new SWTIFrameName(MAIN_FRAME_NAME, 0);

	private nsIDOMNode iframeNode;

	public SWTIFrameName() {
		super();
	}

	public static void main(String[] args) {
		IFrameName f = new SWTIFrameName("111", 0);
		IFrameName f2 = new SWTIFrameName("222", 0);
		f2.setParentFrameName(f);
		IFrameName testF = f2;
		while (testF != null) {
			System.out.println(testF);
			testF = testF.getParentFrameName();
		}
	}

	public SWTIFrameName(String frameName, int index) {
		super(frameName, index);
	}

	/**
	 * @return the iframeNode
	 */
	public nsIDOMNode getIframeNode() {
		return iframeNode;
	}

	/**
	 * @param iframeNode
	 *            the iframeNode to set
	 */
	public void setIframeNode(nsIDOMNode iframeNode) {
		this.iframeNode = iframeNode;
	}

	@Override
	public IFrameName getDefaultParent() {
		return TOP_FRAME;
	}

	@Override
	public DomNode getDomNode() {
		if (iframeNode == null)
			return null;
		final DomNode[] ret = new DomNode[1];
 SWTMozilla.getDefaultInstance().syncExec(new Runnable() {
			
 @Override
 public void run() {
		ret[0] = SWTMozilla.element2dom(iframeNode);
			}
		});
		return ret[0];
	}

}

package javax.swing.plaf.basic;

import javax.swing.JDialog;
import javax.swing.JToolBar;
import javax.swing.RootPaneContainer;

public class ResizeableToolbarUI extends BasicToolBarUI {
	public ResizeableToolbarUI() {
		super();
	}

	@Override
	protected DragWindow createDragWindow(JToolBar toolbar) {
		dragWindow = super.createDragWindow(toolbar);
//		
//        boolean testvalid = false;
//
//		synchronized (dragWindow) {
//
//			DialogPeer peer = (DialogPeer) dragWindow.getPeer();
//			if (peer != null) {
//				peer.setResizable(true);
//				testvalid = true;
//			}
//		}
//
//		// On some platforms, changing the resizable state affects
//		// the insets of the Dialog. If we could, we'd call invalidate()
//		// from the peer, but we need to guarantee that we're not holding
//		// the Dialog lock when we call invalidate().
//		if (testvalid && dragWindow.isValid()) {
//			dragWindow.invalidate();
//		}

		return dragWindow;
	}
	
//	
	@Override
	protected RootPaneContainer createFloatingWindow(JToolBar toolbar) {
		RootPaneContainer ret = super.createFloatingWindow(toolbar);
		if (ret instanceof JDialog)
			((JDialog) ret).setResizable(true);
		
		return ret;
	}

}

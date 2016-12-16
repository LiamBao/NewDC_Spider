package com.cic.datacrawl.ui;

import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;
import com.cic.datacrawl.core.browser.SwingBrowser;

public class HelpPanel extends JPanel {
	private static final Logger LOG = Logger.getLogger(HelpPanel.class);
	private static final long serialVersionUID = 3396008272361984003L;

	private SwingGui swingGui;

	private AbstractJavaWebBrowser helpBrowser = null;

	public HelpPanel(SwingGui swingGui) {
		this.swingGui = swingGui;
	}

	public void startup() {
		LOG.debug("Help Panel is startup");
		final SwingBrowser swingBrowser = new SwingBrowser(this);
		swingBrowser.setBrowser("", true);
		helpBrowser = swingBrowser.getWebBrowser();

		helpBrowser.registerBrowserFunction(this, "insertJavascript");
		helpBrowser.setImageEnabled(true);
		helpBrowser.setUrl("http://192.168.1.12/Help/help.html");
//		helpBrowser.ensureLoadComplete();		

	}

	public Object insertJavascript(Object[] arguments) {
		String jsSource = (String) arguments[0];
		FileWindow currentWindow = swingGui.getSelectedFrame();
		if (currentWindow != null) {
			JTextComponent textComponent = currentWindow.getTextArea();

			try {
				textComponent.getDocument().insertString(
						textComponent.getSelectionStart(), jsSource, null);
			} catch (BadLocationException e) {
			}
		}

		return null;
	}
}

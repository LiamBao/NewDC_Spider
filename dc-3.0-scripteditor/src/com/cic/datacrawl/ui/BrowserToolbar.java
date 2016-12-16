/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BrowserToolbar.java
 *
 * Created on 2010-2-8, 14:03:00
 */

package com.cic.datacrawl.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;

/**
 * 
 * @author rex.wu
 */
public class BrowserToolbar extends javax.swing.JToolBar {

	private static final long serialVersionUID = 5442708344350748490L;

	/** Creates new form BrowserToolbar */
	public BrowserToolbar(BrowserXmlPanel browserXmlPanel) {
		this.browserXmlPanel = browserXmlPanel;
		initComponents();
	}

	/** Creates new form BrowserToolbar */
	private BrowserToolbar() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		btnBack = new javax.swing.JButton();
		btnForward = new javax.swing.JButton();
		btnStop = new javax.swing.JButton();
		btnRefresh = new javax.swing.JButton();
		lblURL = new javax.swing.JLabel();
		cmbURL = new javax.swing.JComboBox();
		btnGO = new javax.swing.JButton();
		btnPutContent = new javax.swing.JButton();

		setName("Form"); // NOI18N
		setLayout(new java.awt.GridBagLayout());

		btnBack.setIcon(new ImageIcon(this.getClass().getClassLoader()
				.getResource("arrow_left.png"))); // NOI18N
		btnBack.setText(""); // NOI18N
		btnBack.setToolTipText("Back"); // NOI18N
		btnBack.setName("btnBack"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		add(btnBack, gridBagConstraints);
		btnBack.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnBackActionPerformed(evt);
			}
		});

		btnForward.setIcon(new ImageIcon(this.getClass().getClassLoader()
				.getResource("arrow_right.png"))); // NOI18N
		btnForward.setText(""); // NOI18N
		btnForward.setToolTipText("Forward"); // NOI18N
		btnForward.setName("btnForward"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		add(btnForward, gridBagConstraints);
		btnForward.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnForwardActionPerformed(evt);
			}
		});

		btnStop.setIcon(new ImageIcon(this.getClass().getClassLoader()
				.getResource("shape_square.png"))); // NOI18N
		btnStop.setText(""); // NOI18N
		btnStop.setToolTipText("Stop"); // NOI18N
		btnStop.setName("btnStop"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		// add(btnStop, gridBagConstraints);
		btnStop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnStopActionPerformed(evt);
			}
		});

		btnRefresh.setIcon(new ImageIcon(this.getClass().getClassLoader()
				.getResource("arrow_refresh.png"))); // NOI18N
		btnRefresh.setText(""); // NOI18N
		btnRefresh.setToolTipText("Refresh"); // NOI18N
		btnRefresh.setName("btnRefresh"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		add(btnRefresh, gridBagConstraints);
		btnRefresh.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnRefreshActionPerformed(evt);
			}
		});

		btnPutContent.setIcon(new ImageIcon(this.getClass().getClassLoader()
				.getResource("application_put.png"))); // NOI18N
		btnPutContent.setText(""); // NOI18N
		btnPutContent.setToolTipText("Reload Content"); // NOI18N
		btnPutContent.setName("btnPutContent"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		add(btnPutContent, gridBagConstraints);
		btnPutContent.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnPutContentActionPerformed(evt);
			}
		});

		lblURL.setText("URL:"); // NOI18N
		lblURL.setName("lblURL"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
		add(lblURL, gridBagConstraints);

		cmbURL.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {
				cmbURL.setMinimumSize(new Dimension(20,
						cmbURL.getMinimumSize().height));

				JTextField comboEditor = (JTextField) cmbURL.getEditor()
						.getEditorComponent();
				if (!comboEditor.hasFocus()) {
					comboEditor.select(0, 0);
					cmbURL.transferFocusBackward();
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
		cmbURL.setEditable(true);
		cmbURL.setName("cmbURL"); // NOI18N
		cmbURL.getEditor().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if (command != null && command.trim().length() > 0)
					changeURL(command);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
		add(cmbURL, gridBagConstraints);

		btnGO.setIcon(new ImageIcon(this.getClass().getClassLoader()
				.getResource("accept.png"))); // NOI18N
		btnGO.setText(""); // NOI18N
		btnGO.setToolTipText("GO"); // NOI18N
		btnGO.setName("btnGO"); // NOI18N
		btnGO.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnGOActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
		add(btnGO, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	private void btnPutContentActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnPutContentActionPerformed
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				browserXmlPanel.reloadContent();
			}
		});
	}// GEN-LAST:event_btnPutContentActionPerformed

	private void btnStopActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnStopActionPerformed
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				browser.stop();
			}
		});
	}// GEN-LAST:event_btnStopActionPerformed

	private void btnForwardActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnForwardActionPerformed
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				browser.execute("history.forward()");
			}
		});
	}// GEN-LAST:event_btnForwardActionPerformed

	private void btnBackActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnBackActionPerformed
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				browser.execute("history.back()");
			}
		});
	}// GEN-LAST:event_btnBackActionPerformed

	private void btnRefreshActionPerformed(ActionEvent evt) {// GEN-FIRST:event_btnRefreshActionPerformed
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				browser.execute("location.reload()");
			}
		});
	}// GEN-LAST:event_btnRefreshActionPerformed

	private void btnGOActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnGOActionPerformed
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				changeURL((String) cmbURL.getEditor().getItem());
			}
		});
	}// GEN-LAST:event_btnGOActionPerformed

	private AbstractJavaWebBrowser browser;

	/**
	 * @return the browser
	 */
	public AbstractJavaWebBrowser getBrowser() {
		return browser;
	}

	/**
	 * @param browser
	 *            the browser to set
	 */
	public void setBrowser(AbstractJavaWebBrowser browser) {
		this.browser = browser;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnBack;
	private javax.swing.JButton btnForward;
	private javax.swing.JButton btnStop;
	private javax.swing.JButton btnPutContent;
	private javax.swing.JButton btnGO;
	private javax.swing.JButton btnRefresh;
	private javax.swing.JComboBox cmbURL;
	private javax.swing.JLabel lblURL;
	// End of variables declaration//GEN-END:variables

	private BrowserXmlPanel browserXmlPanel;

	public void changeURL(final String url) {
		new Thread(new Runnable() {			
			@Override
			public void run() {
				browser.setUrl(url);
			}
		}).start();
	}

	public void addURL(String url) {
		cmbURL.removeItem(url);
		cmbURL.insertItemAt(url, 0);
		cmbURL.getEditor().setItem(url);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.add(new BrowserToolbar());
		f.pack();
		f.setSize(600, 60);
		f.setVisible(true);
	}
}
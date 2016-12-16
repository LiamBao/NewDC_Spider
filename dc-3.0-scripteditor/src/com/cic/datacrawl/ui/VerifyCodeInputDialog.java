package com.cic.datacrawl.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cic.datacrawl.core.jsfunction.RhinoStandardFunction;
import com.cic.datacrawl.core.rhino.VerifyCodeInputer;

public class VerifyCodeInputDialog extends JDialog {

	public static void init() {
		RhinoStandardFunction.verifyCodeInputer = new VerifyCodeInputer() {
			public String showInput(Image image) {
				return VerifyCodeInputDialog.showInput(image);
			}
		};
	}

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JTextField txtValue = null;

	private JLabel imgVerifyCode = null;

	private JPanel jPanel = null;

	private JButton jButton = null;

	private JButton jButton1 = null;

	/**
	 * @param owner
	 */
	public VerifyCodeInputDialog(Frame owner) {
		super(owner, true);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			imgVerifyCode = new JLabel();
			imgVerifyCode.setText("");
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(imgVerifyCode, BorderLayout.CENTER);
			jContentPane.add(getJPanel(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes txtValue
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtValue() {
		if (txtValue == null) {
			txtValue = new JTextField();
			txtValue.setPreferredSize(new Dimension(80, 22));
		}
		return txtValue;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new FlowLayout());
			jPanel.add(getTxtValue(), null);
			jPanel.add(getJButton(), null);
			jPanel.add(getJButton1(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("OK");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					VerifyCodeInputDialog.this.setVisible(false);
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Cancel");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					txtValue.setText(null);
					VerifyCodeInputDialog.this.setVisible(false);
				}
			});
		}
		return jButton1;
	}

	public static String showInput(Image image) {
		VerifyCodeInputDialog dlg = new VerifyCodeInputDialog(null);
		dlg.txtValue.setText(null);
		dlg.imgVerifyCode.setIcon(new ImageIcon(image));
		dlg.setVisible(true);

		String tsValue = dlg.txtValue.getText();
		if ((tsValue != null) && tsValue.isEmpty())
			tsValue = null;
		dlg.dispose();
		return tsValue;
	}

}

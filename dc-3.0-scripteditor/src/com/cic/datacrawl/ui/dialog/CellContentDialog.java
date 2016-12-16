/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CellContentDilaog.java
 *
 * Created on 2010-7-14, 18:14:06
 */

package com.cic.datacrawl.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.mozilla.javascript.xmlimpl.ObjectUtils;

/**
 * 
 * @author rex.wu
 */
public class CellContentDialog extends javax.swing.JDialog {

	/** Creates new form CellContentDilaog */
	private CellContentDialog(java.awt.Frame parent, boolean modal, String title) {
		super(parent, modal);
		initComponents(title);
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, String content) {
		this(parent, modal, title);
		txtContent.setText(content);
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, byte content) {
		this(parent, modal, title);
		txtContent.setText("" + content);
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, short content) {
		this(parent, modal, title);
		txtContent.setText("" + content);
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, int content) {
		this(parent, modal, title);
		txtContent.setText(String.valueOf(content));
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, long content) {
		this(parent, modal, title);
		txtContent.setText(String.valueOf(content));
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, double content) {
		this(parent, modal, title);
		txtContent.setText(String.valueOf(content));
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, float content) {
		this(parent, modal, title);
		txtContent.setText(String.valueOf(content));
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, boolean content) {
		this(parent, modal, title);
		txtContent.setText(String.valueOf(content));
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, char content) {
		this(parent, modal, title);
		txtContent.setText(String.valueOf(content));
	}

	public CellContentDialog(java.awt.Frame parent, boolean modal, String title, Object content) {
		this(parent, modal, title);
		txtContent.setText(ObjectUtils.toString(content));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents(String title) {

		java.awt.GridBagConstraints gridBagConstraints;

		jScrollPane1 = new javax.swing.JScrollPane();
		txtContent = new javax.swing.JTextArea();
		btnClose = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("Form"); // NOI18N
		setTitle(title);
		getContentPane().setLayout(new java.awt.GridBagLayout());

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		txtContent.setLineWrap(true);
		txtContent.setColumns(20);
		txtContent.setEditable(false);
		txtContent.setRows(5);

		txtContent.setName("txtContent"); // NOI18N
		jScrollPane1.setViewportView(txtContent);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		getContentPane().add(jScrollPane1, gridBagConstraints);

		btnClose.setMnemonic('c');
		btnClose.setText("Close"); // NOI18N
		btnClose.setName("btnClose"); // NOI18N
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				closeButtonAction(actionevent);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
		getContentPane().add(btnClose, gridBagConstraints);
		addEscKeyListener();
		setSize(800, 600);
	}// </editor-fold>


	private KeyAdapter escKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent ke) {
			int code = ke.getKeyCode();
			if (code == KeyEvent.VK_ESCAPE) {
				ke.consume();
				setVisible(false);
			}
		}
	};

	private void addEscKeyListener() {
		addKeyListener(escKeyListener);

		txtContent.addKeyListener(escKeyListener);
		btnClose.addKeyListener(escKeyListener);
	}
	
	private void closeButtonAction(ActionEvent actionevent) {
		this.setVisible(false);
		this.dispose();
	}

	// /**// * @param args the command line arguments
	// */
	// public static void main(String args[]) {
	// java.awt.EventQueue.invokeLater(new Runnable() {
	// public void run() {
	// CellContentDilaog dialog = new CellContentDilaog(new
	// javax.swing.JFrame(), true);
	// dialog.addWindowListener(new java.awt.event.WindowAdapter() {
	// public void windowClosing(java.awt.event.WindowEvent e) {
	// System.exit(0);
	// }
	// });
	// dialog.setVisible(true);
	// }
	// });
	// }

	// Variables declaration - do not modify
	private javax.swing.JButton btnClose;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea txtContent;
	// End of variables declaration//GEN-END:variables

}

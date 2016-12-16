/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ChooseWorkspaceDialog.java
 *
 * Created on 2010-1-13, 14:18:42
 */

package com.cic.datacrawl.ui.dialog;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.cic.datacrawl.core.config.Config;

/**
 * `
 * 
 * @author rex.wu
 */
public class ChooseWorkspaceDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5761998894462858086L;

	private JFrame parent;

	/** Creates new form ChooseWorkspaceDialog */
	public ChooseWorkspaceDialog(JFrame parent) {
		super(parent);
		this.parent = parent;
		System.setProperty("mode.edit", "" + true);
		initComponents();
	}

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

		btnCancel.addKeyListener(escKeyListener);
		btnChoose.addKeyListener(escKeyListener);
		btnOK.addKeyListener(escKeyListener);
		txtWorkspace.addKeyListener(escKeyListener);
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

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		txtWorkspace = new javax.swing.JComboBox();
		btnChoose = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		btnCancel = new javax.swing.JButton();
		btnOK = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Choose Workspace"); // NOI18N
		setName("Form"); // NOI18N
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent evt) {
				formWindowClosed(evt);
			}
		});
		getContentPane().setLayout(new java.awt.GridBagLayout());

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose Workspace")); // NOI18N
		jPanel1.setName("jPanel1"); // NOI18N
		jPanel1.setLayout(new java.awt.GridBagLayout());

		jLabel1.setText("Workspace:"); // NOI18N
		jLabel1.setName("jLabel1"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
		jPanel1.add(jLabel1, gridBagConstraints);
		txtWorkspace.setEditable(true);
		String workspace = Config.getInstance().getWorkspace();
		String[] workspaces = workspace.split(";");
		if (workspaces != null && workspaces.length > 0) {
			for (int i = 0; i < workspaces.length; ++i) {
				if (workspaces[i] != null && workspaces[i].trim().length() > 0)
					txtWorkspace.addItem(workspaces[i]);
			}
			txtWorkspace.setSelectedIndex(0);
		}

		txtWorkspace.setName("txtWorkspace"); // NOI18N

		txtWorkspace.getEditor().selectAll();
		txtWorkspace.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				txtWorkspace.getEditor().selectAll();
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
		jPanel1.add(txtWorkspace, gridBagConstraints);

		btnChoose.setText("..."); // NOI18N
		btnChoose.setName("btnChoose"); // NOI18N
		btnChoose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnChooseActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel1.add(btnChoose, gridBagConstraints);

		jLabel2.setText("Script Editor stores your projects in a folder called a workspace."); // NOI18N
		jLabel2.setName("jLabel2"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel1.add(jLabel2, gridBagConstraints);

		jLabel3.setText("Choose a workspace folder to use for this session."); // NOI18N
		jLabel3.setName("jLabel3"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel1.add(jLabel3, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		getContentPane().add(jPanel1, gridBagConstraints);

		btnCancel.setText("Cancel"); // NOI18N
		btnCancel.setName("btnCancel"); // NOI18N
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCancelActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 5, 5);
		getContentPane().add(btnCancel, gridBagConstraints);

		btnOK.setText("Confirm"); // NOI18N
		btnOK.setName("btnOK"); // NOI18N
		btnOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnOKActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 326, 5, 0);
		getContentPane().add(btnOK, gridBagConstraints);
		addEscKeyListener();
		pack();
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		java.awt.Dimension dialogSize = getSize();
		setLocation((screenSize.width - dialogSize.width) / 2, (screenSize.height - dialogSize.height) / 2);
	}// </editor-fold>//GEN-END:initComponents

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCancelActionPerformed
		System.exit(0);
	}// GEN-LAST:event_btnCancelActionPerformed

	private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnOKActionPerformed
		String path = (String) txtWorkspace.getSelectedItem();
		File f = new File(path);
		if (!f.exists()) {
			int result = JOptionPane.showConfirmDialog(this, "Do you want to create new folder?",
														"Folder is not exists", JOptionPane.YES_NO_OPTION,
														JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				f.mkdirs();
			} else {
				txtWorkspace.requestFocus();
				return;
			}
		} else if (f.isFile()) {
			JOptionPane.showMessageDialog(this, "Invalid Workspace", "Invalid Workspace",
											JOptionPane.ERROR_MESSAGE);
			return;
		}

		String workspaceHome = Config.getInstance().getWorkspace();
		String thePath = ";" + path + ";";
		if (workspaceHome.indexOf(thePath) >= 0) {
			String[] workspaces = workspaceHome.split(thePath);
			StringBuilder workspace = new StringBuilder();
			if (workspaces != null && workspaces.length > 0) {
				for (int i = 0; i < workspaces.length; ++i) {
					if (workspaces[i] != null && workspaces[i].trim().length() > 0){
						if(i>0)
							workspace.append(";");
						workspace.append(workspaces[i]);
					}
				}
			}
		}
		workspaceHome = thePath + workspaceHome.substring(1);

		Config.getInstance().setWorkspaceHome(workspaceHome);
		try {
			Config.getInstance().save();
		} catch (IOException e) {
		}

		getDlgChoose().setVisible(false);
		this.setVisible(false);

		getDlgChoose().dispose();
		this.dispose();

		// WindowUtils.showFrameAtScreenCenter(parent);
		parent.setVisible(true);
	}// GEN-LAST:event_btnOKActionPerformed

	private void btnChooseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnChooseActionPerformed
		getDlgChoose().setVisible(true);
	}// GEN-LAST:event_btnChooseActionPerformed

	private void formWindowClosed(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosed
		// TODO add your handling code here:
	}// GEN-LAST:event_formWindowClosed

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		ChooseWorkspaceDialog dialog = new ChooseWorkspaceDialog(new JFrame());
		dialog.setVisible(true);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnChoose;
	private javax.swing.JButton btnOK;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JComboBox txtWorkspace;
	private ChooseFolderDialog dlgChoose;

	private String[] args;

	// End of variables declaration//GEN-END:variables
	/**
	 * @return the dlgChoose
	 */
	protected ChooseFolderDialog getDlgChoose() {
		if (dlgChoose == null) {
			dlgChoose = new ChooseFolderDialog(this);
			dlgChoose.showSelectPath((String) txtWorkspace.getSelectedItem());
		}
		return dlgChoose;
	}

	public void setPath(String path) {
		if (path == null || path.trim().length() == 0)
			txtWorkspace.getEditor().setItem("");
		txtWorkspace.getEditor().setItem(path);
		// int index = path.length();
		txtWorkspace.requestFocus();
		txtWorkspace.getEditor().selectAll();
		txtWorkspace.addItem(path);
		txtWorkspace.setSelectedItem(path);
	}

	public void setStartupArgs(String[] args) {
		this.args = args;
	}

}

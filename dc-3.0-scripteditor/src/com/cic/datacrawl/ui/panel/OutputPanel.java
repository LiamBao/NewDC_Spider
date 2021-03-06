/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OutputPanel.java
 *
 * Created on 2010-7-12, 18:10:42
 */

package com.cic.datacrawl.ui.panel;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.ui.JSPopupMenu;
import com.cic.datacrawl.ui.tools.CommandConstants;

/**
 * 
 * @author rex.wu
 */
public class OutputPanel extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8247211231561622416L;

	/** Creates new form OutputPanel */
	public OutputPanel() {
		initComponents();
	}

	/**
	 * The popup menu.
	 */
	private JSPopupMenu popup;

	// private JTabbedPane tabPanel;

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent event) {
				Component component = tabbedPane.getSelectedComponent();
				if (component != null) {
					rows = ((ResultPanel) component).getRowCount();
					lblTotalNumber.setText(String.valueOf(rows));

					lblCurrentNumber.setText(String.valueOf(((ResultPanel) tabbedPane.getSelectedComponent())
							.getSelectedRowIndex() + 1));
				}
			}
		});
		java.awt.GridBagConstraints gridBagConstraints;

		pnlStatus = new javax.swing.JPanel();
		lblEmpty = new javax.swing.JLabel();
		lblTotal = new javax.swing.JLabel();
		pnlTotal = new javax.swing.JPanel();
		lblTotalNumber = new javax.swing.JLabel();
		lblTotalText = new javax.swing.JLabel();
		pnlCurrent = new javax.swing.JPanel();
		lblCurrentNumber = new javax.swing.JLabel();
		lblCurrentText = new javax.swing.JLabel();
		lblCurrent = new javax.swing.JLabel();

		setName("Form"); // NOI18N
		setLayout(new java.awt.GridBagLayout());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(tabbedPane, gridBagConstraints);

		pnlStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		pnlStatus.setName("pnlStatus"); // NOI18N
		pnlStatus.setLayout(new java.awt.GridBagLayout());

		lblEmpty.setText(""); // NOI18N
		lblEmpty.setName("lblEmpty"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		pnlStatus.add(lblEmpty, gridBagConstraints);

		lblTotal.setText("Total"); // NOI18N
		lblTotal.setName("lblTotal"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
		pnlStatus.add(lblTotal, gridBagConstraints);

		pnlTotal.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		pnlTotal.setName("pnlTotal"); // NOI18N
		pnlTotal.setLayout(new java.awt.GridBagLayout());

		lblTotalNumber.setText("0"); // NOI18N
		lblTotalNumber.setName("lblTotalNumber"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 4);
		pnlTotal.add(lblTotalNumber, gridBagConstraints);

		lblTotalText.setText("Rows"); // NOI18N
		lblTotalText.setName("lblTotalText"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
		pnlTotal.add(lblTotalText, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
		pnlStatus.add(pnlTotal, gridBagConstraints);

		pnlCurrent.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		pnlCurrent.setName("pnlCurrent"); // NOI18N
		pnlCurrent.setLayout(new java.awt.GridBagLayout());

		lblCurrentNumber.setText("0"); // NOI18N
		lblCurrentNumber.setName("lblCurrentNumber"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
		pnlCurrent.add(lblCurrentNumber, gridBagConstraints);

		lblCurrentText.setText("th"); // NOI18N
		lblCurrentText.setName("lblCurrentText"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
		pnlCurrent.add(lblCurrentText, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
		pnlStatus.add(pnlCurrent, gridBagConstraints);

		lblCurrent.setText("Current:"); // NOI18N
		lblCurrent.setName("lblCurrent"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
		pnlStatus.add(lblCurrent, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		add(pnlStatus, gridBagConstraints);

		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		ExportMenuListener exportMenuListener = new ExportMenuListener(tabbedPane);
		popup = new JSPopupMenu(CommandConstants.GROUP_MENU_EXPORT, exportMenuListener);

		popup.addPopupMenuListener(exportMenuListener);

	}// </editor-fold>//GEN-END:initComponents

	/**
	 * Checks if the popup menu should be shown.
	 * 
	 * @param b
	 */
	private void checkPopup(MouseEvent e) {
		if (e.isPopupTrigger() && tabbedPane.getComponentCount() > 0)
			popup.show(this, e.getX(), e.getY());

	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	// End of variables declaration//GEN-END:variables
	//
	public void addItem(BaseEntity[] items) {
		if (items != null) {
			for (int i = 0; i < items.length; ++i) {
				if (i == items.length - 1)
					addItem(items[i], false);
				else
					addItem(items[i], false);
			}
			// lblTotalNumber.setText(String.valueOf(rows));
		}
	}

	private ListSelectionListener createSelectionListener() {
		return new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				lblCurrentNumber.setText(String.valueOf(((DefaultListSelectionModel) e.getSource())
						.getMaxSelectionIndex() + 1));
			}
		};
	}

	private long rows = 0;

	private void addItem(BaseEntity item, boolean activityTab) {
		if (item == null)
			return;
		ResultPanel resultPanel = getResultPanel(item);
		resultPanel.addItem(item);
		if (resultPanel == tabbedPane.getSelectedComponent()) {
			++rows;
		}

		lblTotalNumber.setText(String.valueOf(rows));
		if (activityTab)
			tabbedPane.setSelectedComponent(resultPanel);
	}

	public void addItem(BaseEntity item) {
		addItem(item, false);
	}

	@Override
	public void removeAll() {
		synchronized (resultPanelMap) {
			Iterator<String> keyIterator = resultPanelMap.keySet().iterator();
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				ResultPanel panel = resultPanelMap.get(key);
				panel.removeAll();
			}
			resultPanelMap.clear();

		}
		rows = 0;
		lblTotalNumber.setText(String.valueOf(rows));
		tabbedPane.removeAll();
	}

	private ResultPanel getResultPanel(BaseEntity item) {
		ResultPanel panel = null;
		synchronized (resultPanelMap) {
			if (resultPanelMap.containsKey(item.getTheEntityName())) {
				panel = resultPanelMap.get(item.getTheEntityName());
			} else {
				panel = new ResultPanel(item);
				panel.addRowSelectionChangedListener(createSelectionListener());
				panel.setPopup(popup);
				tabbedPane.addTab(item.getTheEntityName(), panel);
				resultPanelMap.put(item.getTheEntityName(), panel);
			}
		}
		// tabbedPane.setSelectedComponent(panel);
		return panel;
	}

	private HashMap<String, ResultPanel> resultPanelMap = new HashMap<String, ResultPanel>();

	private JTabbedPane tabbedPane;
	private javax.swing.JLabel lblEmpty;

	private javax.swing.JLabel lblCurrent;
	private javax.swing.JLabel lblCurrentNumber;
	private javax.swing.JLabel lblCurrentText;
	private javax.swing.JLabel lblTotal;
	private javax.swing.JLabel lblTotalNumber;
	private javax.swing.JLabel lblTotalText;
	private javax.swing.JPanel pnlCurrent;
	private javax.swing.JPanel pnlTotal;
	private javax.swing.JPanel pnlStatus;

}

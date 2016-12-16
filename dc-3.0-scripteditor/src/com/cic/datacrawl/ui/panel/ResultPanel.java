/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ResultPanel.java
 *
 * Created on 2010-7-12, 18:18:30
 */

package com.cic.datacrawl.ui.panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.ui.JSPopupMenu;
import com.cic.datacrawl.ui.SwingGui;
import com.cic.datacrawl.ui.dialog.CellContentDialog;

/**
 * 
 * @author rex.wu
 */
public class ResultPanel extends javax.swing.JPanel {

	/** Creates new form ResultPanel */
	private ResultPanel() {
		super();
	}

	public ResultPanel(BaseEntity item) {
		this();

		initComponents(item);
	}

	public void addRowSelectionChangedListener(ListSelectionListener listener) {
		tblResult.getSelectionModel().addListSelectionListener(listener);
	}

	public long getSelectedRowIndex() {
		return tblResult.getSelectedRow();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents(BaseEntity item) {

		pnlScroll = new javax.swing.JScrollPane();
		tblResult = new javax.swing.JTable();

		setName("Form"); // NOI18N
		setLayout(new java.awt.BorderLayout());

		pnlScroll.setName("pnlScroll"); // NOI18N
		tableModel = new ResultTableModel(item);
		tblResult.setModel(tableModel);
		tblResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblResult.setName("tblResult"); // NOI18N

		tblResult.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		tblResult.getTableHeader().setReorderingAllowed(false);

		TableRowSorter<ResultTableModel> rowSorter = new TableRowSorter<ResultTableModel>(tableModel);
		int columnCount = tableModel.getColumnCount();
		for (int i = 0; i < columnCount; ++i) {
			Object value = item.get(tableModel.getColumnName(i));
			if (value instanceof Date) {
				rowSorter.setComparator(i, getDateComparator());
			} else {
				rowSorter.setComparator(i, getComparator());
			}
		}
MouseListener exportPopupMenuListener = new MouseListener() {

	@Override
	public void mouseReleased(MouseEvent mouseevent) {
		checkPopup(mouseevent);
	}

	@Override
	public void mousePressed(MouseEvent mouseevent) {
		checkPopup(mouseevent);

	}

	@Override
	public void mouseExited(MouseEvent mouseevent) {

	}

	@Override
	public void mouseEntered(MouseEvent mouseevent) {

	}

	@Override
	public void mouseClicked(MouseEvent mouseevent) {
		tableMouseClicked(mouseevent);
	}
};
		tblResult.addMouseListener(exportPopupMenuListener);
		addMouseListener(exportPopupMenuListener);

		tblResult.setRowSorter(rowSorter);
		pnlScroll.setViewportView(tblResult);

		add(pnlScroll, java.awt.BorderLayout.CENTER);
	}// </editor-fold>//GEN-END:initComponents

	@Override
	public void removeAll() {
		tableModel.clear();
		super.removeAll();
	}

	private void tableMouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
			int selectedColumn = tblResult.getSelectedColumn();
			int selectRow = tblResult.getSelectedRow();
			Object value = tblResult.getValueAt(selectRow, selectedColumn);
			String title = tblResult.getColumnName(selectedColumn);
			CellContentDialog dialog = new CellContentDialog(SwingGui.getInstance(), true, title, value);
			dialog.setLocationRelativeTo(SwingGui.getInstance());
			dialog.setVisible(true);
		} else if (evt.isPopupTrigger()) {
			checkPopup(evt);
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane pnlScroll;
	private javax.swing.JTable tblResult;
	private ResultTableModel tableModel;

	/**
	 * The popup menu.
	 */
	private JSPopupMenu popup;

	/**
	 * @param popup
	 *            the popup to set
	 */
	public void setPopup(JSPopupMenu popup) {
		this.popup = popup;
	}

	/**
	 * Checks if the popup menu should be shown.
	 * 
	 * @param b
	 */
	private void checkPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			if (tblResult.getRowCount() > 0)
				popup.show(SwingGui.getInstance(), e.getXOnScreen(), e.getYOnScreen());
		}
	}

	// End of variables declaration//GEN-END:variables
	public void addItem(BaseEntity item) {
		tableModel.addEntity(item);
	}
	
	public List<BaseEntity> getAllResults() {
		return tableModel.getAllValues();
	}

	public long getRowCount() {
		return tableModel.getRowCount();
	}

	private Comparator<Object> comparator;
	private Comparator<Date> dateComparator;

	private Comparator<Date> getDateComparator() {
		if (dateComparator == null) {
			dateComparator = new Comparator<Date>() {
				@Override
				public int compare(Date d1, Date d2) {
					if (d1.getTime() > d2.getTime()) {
						return 1;
					} else if (d1.getTime() < d2.getTime()) {
						return -1;
					} else {
						return 0;
					}
				}
			};
		}
		return dateComparator;
	}

	private Comparator<Object> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<Object>() {

				@Override
				public int compare(Object o1, Object o2) {
					// TODO Auto-generated method stub
					long longValue1 = Long.MIN_VALUE;
					long longValue2 = Long.MIN_VALUE;
					double doubleValue1 = Double.MIN_VALUE;
					double doubleValue2 = Double.MIN_VALUE;
					String stringValue1 = ObjectUtils.toString(o1);
					String stringValue2 = ObjectUtils.toString(o2);
					boolean isNumberObject = true;

					if (o1 instanceof Number) {
						Number number = (Number) o1;
						longValue1 = number.longValue();
						doubleValue1 = number.doubleValue();
						isNumberObject = true;
					} else {
						try {
							doubleValue1 = Double.parseDouble(stringValue1);
							longValue1 = Long.parseLong(stringValue1);
							isNumberObject = true;
						} catch (Throwable t) {
							try {
								doubleValue1 = Double.parseDouble(stringValue1);
								longValue1 = Double.doubleToLongBits(doubleValue1);
								isNumberObject = true;
							} catch (Throwable t1) {
								isNumberObject = false;
							}
						}
					}

					if (o2 instanceof Number) {
						Number number = (Number) o2;
						longValue2 = number.longValue();
						doubleValue2 = number.doubleValue();
						isNumberObject = isNumberObject & true;
					} else {
						try {
							doubleValue2 = Double.parseDouble(stringValue2);
							longValue2 = Long.parseLong(stringValue2);
							isNumberObject = isNumberObject & true;
						} catch (Throwable t) {
							try {
								doubleValue2 = Double.parseDouble(stringValue2);
								longValue2 = Double.doubleToLongBits(doubleValue2);
								isNumberObject = isNumberObject & true;
							} catch (Throwable t1) {
								isNumberObject = false;
							}
						}
					}

					if (isNumberObject) {
						if (longValue1 > longValue2 || doubleValue1 > doubleValue2) {
							return 1;
						} else if (longValue1 < longValue2 || doubleValue1 < doubleValue2) {
							return -1;
						} else {
							return 0;
						}
					} else {
						return stringValue1.compareTo(stringValue2);
					}
				}
			};
		}
		return comparator;
	}

}
package com.cic.datacrawl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;

import com.cic.datacrawl.ui.tools.CommandConstants;
import com.cic.datacrawl.ui.tools.ItemDesc;

/**
 * A table for evaluated expressions.
 */
public class Evaluator extends JTable implements ActionListener,
		PopupMenuListener, MouseListener {

	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = 8133672432982594256L;
	/**
	 * The popup menu.
	 */
	private JSPopupMenu popup;

	/**
	 * Checks if the popup menu should be shown.
	 * 
	 * @param b
	 */
	private void checkPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			String status = getPopMenuStatus();
			if (status == null)
				popup.resetStatus();
			else
				popup.setStatus(status);
			popup.show(this, e.getX(), e.getY());
		}
	}

	// MouseListener

	/**
	 * Called when a mouse button is pressed.
	 */
	public void mousePressed(MouseEvent e) {
		checkPopup(e);
	}

	/**
	 * Called when the mouse is clicked.
	 */
	public void mouseClicked(MouseEvent e) {
		checkPopup(e);
		requestFocus();
	}

	/**
	 * Called when the mouse enters the component.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Called when the mouse exits the component.
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Called when a mouse button is released.
	 */
	public void mouseReleased(MouseEvent e) {
		checkPopup(e);
	}

	// PopupMenuListener

	/**
	 * Called before the popup menu will become visible.
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}

	/**
	 * Called before the popup menu will become invisible.
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	/**
	 * Called when the popup menu is cancelled.
	 */
	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	/**
	 * The {@link TableModel} for this table.
	 */
	MyTableModel tableModel;
	private SwingGui debugGui;

	/**
	 * Creates a new Evaluator.
	 */
	public Evaluator(MyTableModel myTableModel) {
		super(myTableModel);
		this.debugGui = myTableModel.getDebugGui();
		init();
	}

	/**
	 * Creates a new Evaluator.
	 */
	public Evaluator(SwingGui debugGui) {
		this(new MyTableModel(debugGui));

	}

	@Override
	public void setEnabled(boolean enabled) {
		String status = getPopMenuStatus();
		if (status == null)
			popup.resetStatus();
		else
			popup.setStatus(status);
		super.setEnabled(enabled);
	}

	private String getPopMenuStatus() {
		if (getSelectedRowCount() > 0) {
			if (isEnabled()) {
				return "SELECTED_AND_ENABLED";
			} else {
				return "SELECTED";
			}
		} else {
			if (isEnabled()) {
				return "ENABLED";
			} else {
				return null;
			}

		}
	}

	private void init() {
		tableModel = (MyTableModel) getModel();
		popup = new JSPopupMenu(CommandConstants.GROUP_MENU_EVALUATOR_POPUP,
				this);

		popup.addEnabledButtonGroup("SELECTED", new ItemDesc[] {
				CommandConstants.REMOVE, CommandConstants.REMOVE_ALL });

		popup.addVisibleButtonGroup("SELECTED", new ItemDesc[] {
				CommandConstants.REMOVE, CommandConstants.REMOVE_ALL });

		popup.addEnabledButtonGroup("ENABLED",
				new ItemDesc[] { CommandConstants.ADD_NEW_WATCH });
		popup.addVisibleButtonGroup("ENABLED",
				new ItemDesc[] { CommandConstants.ADD_NEW_WATCH });

		popup.addEnabledButtonGroup("SELECTED_AND_ENABLED",
				CommandConstants.GROUP_MENU_EVALUATOR_POPUP);
		popup.addVisibleButtonGroup("SELECTED_AND_ENABLED",
				CommandConstants.GROUP_MENU_EVALUATOR_POPUP);
		setEnabled(true);
		popup.addPopupMenuListener(this);

		addMouseListener(this);
		this.getTableHeader().addMouseListener(this);		
	}

	public void addWatch(String watchString) {
		String str = null;
		if (watchString == null)
			return;
		str = watchString.trim();
		if (str.length() == 0)
			return;
		int row = getModel().getRowCount();
		setValueAt(str, row - 1, 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		popup.setVisible(false);
		String cmd = e.getActionCommand();
		if (cmd.equals(CommandConstants.REMOVE.getCmd())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					removeSelectedRows();
				}
			});
		} else if (cmd.equals(CommandConstants.REMOVE_ALL.getCmd())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					tableModel.removeAllRows();
				}
			});
		} else if (cmd.equals(CommandConstants.ADD_NEW_WATCH.getCmd())) {
			final String input = MessageDialogWrapper.showInputDialog(debugGui,
					"Please input an expression for watching.",
					"Add new watch", JOptionPane.PLAIN_MESSAGE);
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					if (input != null) {
						String watchString = input.trim();
						if (watchString.length() > 0) {
							addWatch(watchString);
						}
					}
				}
			});
		}
	}

	private void removeSelectedRows() {
		int[] indexs = getSelectedRows();
		Arrays.sort(indexs);
		for (int i = indexs.length - 1; i >= 0; --i) {
			tableModel.remove(indexs[i]);
		}
		repaint();
	}
}

package com.cic.datacrawl.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.rhino.debugger.RhinoDim;

/**
 * Table model class for watched expressions.
 */
public class MyTableModel extends AbstractTableModel {

	private static final Logger logger = Logger.getLogger(MyTableModel.class);
	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = 2971618907207577000L;

	/**
	 * The debugger GUI.
	 */
	private SwingGui debugGui;

	/**
	 * @return the debugGui
	 */
	public SwingGui getDebugGui() {
		return debugGui;
	}

	/**
	 * List of watched expressions.
	 */
	private List<String> expressions;

	/**
	 * List of values from evaluated from {@link #expressions}.
	 */
	private List<String> values;
	private RhinoDim.StackFrame frame;

	/**
	 * Creates a new MyTableModel.
	 */
	public MyTableModel(SwingGui debugGui) {
		this.debugGui = debugGui;
		expressions = Collections.synchronizedList(new ArrayList<String>());
		values = Collections.synchronizedList(new ArrayList<String>());
		expressions.add("");
		values.add("");
	}

	public void changeScope(RhinoDim.StackFrame frame) {
		this.frame = frame;
		updateModel();
	}

	/**
	 * Returns the number of columns in the table (2).
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * Returns the number of rows in the table.
	 */
	public int getRowCount() {
		return expressions.size();
	}

	/**
	 * Returns the name of the given column.
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Expression";
		case 1:
			return "Value";
		}
		return null;
	}

	/**
	 * Returns whether the given cell is editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		switch (column) {
		case 0:
			return true;
		case 1:
			return false;
		}
		return false;
	}

	/**
	 * Returns the value in the given cell.
	 */
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return expressions.get(row);
		case 1:
			return values.get(row);
		}
		return "";
	}

	public void remove(int row) {
		if (expressions.size() > row) {
			expressions.remove(row);
			values.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}

	public void removeAllRows() {
		int max = expressions.size();
		expressions.clear();
		values.clear();
		fireTableRowsDeleted(0, max - 1);
		expressions.add("");
		values.add("");
		fireTableRowsInserted(1, 1);
	}

	/**
	 * Sets the value in the given cell.
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {
		switch (column) {
		case 0:
			String expr = value.toString();
			expressions.set(row, expr);
			String result = executeExpr(expr, row);

			values.set(row, result);
			updateModel();
			if (row + 1 == expressions.size()) {
				expressions.add("");
				values.add("");
				fireTableRowsInserted(row + 1, row + 1);
			}
			break;
		case 1:
			// just reset column 2; ignore edits
			fireTableDataChanged();
		}
	}

	private String executeExpr(String expr, int row) {
		String result = "";
		if (expr != null && expr.length() > 0) {
			if (frame != null) {
				try {
					result = ObjectUtils.toString(debugGui.dim.evalReturnObject(expr, row, frame
							.contextData(), true));
				} catch (Exception e) {
					result = e.getMessage();
				}
			} else {
				try {
					result = debugGui.dim.eval(expr, false);
				} catch (Exception e) {
					result = e.getMessage();
				}
			}
			if (result == null)
				result = "";
		}
		return result;
	}

	/**
	 * Re-evaluates the expressions in the table.
	 */
	void updateModel() {
		for (int i = 0; i < expressions.size(); ++i) {
			String expr = expressions.get(i);
			String result = "";
			if (expr.length() > 0) {
				try {
					result = executeExpr(expr, i);
				} catch (Exception e) {
					MessageDialogWrapper.showMessageDialog(debugGui, e.getMessage(), "Error Compiling ",
															JOptionPane.ERROR_MESSAGE);
				}
				if (result == null)
					result = "";
			} else {
				result = "";
			}
			result = result.replace('\n', ' ');
			values.set(i, result);
		}
		fireTableDataChanged();
	}
}

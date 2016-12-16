package com.cic.datacrawl.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class FunctionTableModel extends AbstractTableModel {
	public static final int SCRIPT_COLUMN_INDEX = 0;
	public static final int FUNCTION_COLUMN_INDEX = 1;
	public static final int FULL_PATH_COLUMN_INDEX = -1;
	public static final int FULL_NAME_COLUMN_INDEX = -2;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1350124326308952400L;

	public FunctionTableModel() {
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public String getColumnName(int col) {
		return headers[col];
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	public String[] getScriptNames() {
		String[] ret = new String[scriptNameList.size()];
		scriptNameList.toArray(ret);
		return ret;
	}

	private ArrayList<String> scriptNameList = new ArrayList<String>();
	private Vector<FunctionDefine> list = new Vector<FunctionDefine>();
	private static final String headers[] = { "Script", "Function" };

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		FunctionDefine define = list.get(rowIndex);
		switch (columnIndex) {
		case SCRIPT_COLUMN_INDEX:
			return define.getScriptName();

		case FUNCTION_COLUMN_INDEX:
			return define.getFunctionName();
		case FULL_PATH_COLUMN_INDEX:
			return define.getScriptPath();
		case FULL_NAME_COLUMN_INDEX:
			return define.getFullName();

		default:
			return define.getFunctionName();
		}
	}

	public void clear() {
		list.clear();
	}

	public void addFunction(String fullName) {
		FunctionDefine define = new FunctionDefine(fullName);
		list.add(define);
		synchronized (scriptNameList) {
			if (!scriptNameList.contains(define.getScriptName()))
				scriptNameList.add(define.getScriptName());
		}
		int row = list.size() - 1;
		this.fireTableDataChanged();
		fireTableRowsInserted(row, row);
	}

	public String getFullFunctionName(int selectedRow) {
		FunctionDefine define = list.get(selectedRow);
		return define.getFullName();
	}

}

class FunctionDefine {
	private String fullName;
	private String scriptPath;

	/**
	 * @return the scriptPath
	 */
	public String getScriptPath() {
		return scriptPath;
	}

	private String scriptName;
	private String functionName;

	public FunctionDefine(String fullName) {
		super();
		this.fullName = fullName;

		int splitIndex = fullName.lastIndexOf(".");
		scriptPath = fullName.substring(0, splitIndex);
		scriptName = scriptPath.substring(scriptPath.lastIndexOf(File.separator) + 1);
		functionName = fullName.substring(splitIndex + 1);
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @return the scriptName
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * @return the functionName
	 */
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public String toString() {
		return getFullName();
	}
}

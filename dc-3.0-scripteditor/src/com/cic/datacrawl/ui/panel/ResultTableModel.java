package com.cic.datacrawl.ui.panel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.cic.datacrawl.core.entity.BaseEntity;

public class ResultTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1350124326308952400L;

	private Class clazz;

	public ResultTableModel(BaseEntity item) {
		headers = item.getColumnNames();
		Arrays.sort(headers);
		clazz = item.getClass();
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

	private Vector<BaseEntity> list = new Vector<BaseEntity>();
	private String[] headers;

	public List<BaseEntity> getAllValues() {
		List<BaseEntity> ret = new ArrayList<BaseEntity>();
		ret.addAll(list);

		return ret;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		BaseEntity entity = list.get(rowIndex);
		return getValueAt(entity, columnIndex);
	}

	private Object getValueAt(BaseEntity entity, int columnIndex) {
		if (columnIndex >= headers.length || columnIndex < 0)
			return null;
		return entity.getString(headers[columnIndex]);
	}

	public void addEntity(BaseEntity entity) {
		list.add(entity);

		int row = list.size() - 1;
		// this.fireTableDataChanged();
		fireTableRowsInserted(row, row);
	}

	public void setEntity(BaseEntity entity, int rowIndex) {
		list.set(rowIndex, entity);
	}

	@Override
	public void setValueAt(Object obj, int rowIndex, int columnIndex) {
		BaseEntity entity = null;
		if (rowIndex >= list.size()) {
			try {
				entity = (BaseEntity) clazz.newInstance();
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		} else {
			entity = list.get(rowIndex);
		}

		if (entity == null) {
			throw new IllegalArgumentException("Can not get instance on the " + rowIndex + "th row.");
		}
		if (columnIndex > headers.length || columnIndex < 0)
			throw new IllegalArgumentException("Invalid columnIndex");

		String key = headers[columnIndex];
		entity.set(key, obj);
	}

	public void clear() {
		list.clear();
	}

}

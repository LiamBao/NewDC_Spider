package com.cic.datacrawl.core.db;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class DbResultRow extends ScriptableObject {

	// Returns the name of this JavaScript class
	@Override
	public String getClassName() {
		return "DbResultRow";
	}

	// Defines all named properties
	@Override
	public boolean has(String name, Scriptable start) {
		if ("length".equals(name))
			return true;
		return resultSet.fieldsIndex.containsKey(name);
	}

	// Defines all numeric properties
	@Override
	public boolean has(int index, Scriptable start) {
		return index >= 0 && index < resultSet.fields.length;
	}

	// Get the named property value.
	@Override
	public Object get(String name, Scriptable start) {
		if ("length".equals(name))
			return new Integer(resultSet.fields.length);
		Integer fieldIndex = resultSet.fieldsIndex.get(name);
		if (fieldIndex == null)
			return NOT_FOUND;
		return resultSet.rows[rowIndex][fieldIndex.intValue()];
	}

	// Get the indexed property.
	@Override
	public Object get(int index, Scriptable start) {
		if (!has(index, start))
			return NOT_FOUND;
		return resultSet.rows[rowIndex][index];
	}

	// ******* Java implement ***************************
	public DbResultRow(DbResultSet resultSet, int rowIndex) {
		this.resultSet = resultSet;
		this.rowIndex = rowIndex;
	}

	private DbResultSet resultSet;
	private int rowIndex;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());

		sb.append("\t");
		sb.append(rowIndex + 1);
		sb.append(" [");
		for (int i = 0; i < resultSet.fields.length; ++i) {
			if (i > 0)
				sb.append(", ");
			String fieldName = resultSet.fields[i].getName();
			Integer fieldIndex = resultSet.fieldsIndex.get(fieldName);
			sb.append(fieldName);
			sb.append(": ");
			sb.append(resultSet.rows[rowIndex][fieldIndex]);
		}
		sb.append("]");

		return sb.toString();
	}
}

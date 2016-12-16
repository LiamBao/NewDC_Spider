package com.cic.datacrawl.core.db;

import org.mozilla.javascript.NativeObjectUtil;

import com.cic.datacrawl.core.util.SQLUtil;

public class BatchPreparedStatementSetterValue {

	private int type;
	private Object value;

	public static BatchPreparedStatementSetterValue newInstance(int type,
			Object value) {

		return new BatchPreparedStatementSetterValue(type, value);
	}

	public BatchPreparedStatementSetterValue(int type, Object value) {
		super();
		this.value = NativeObjectUtil.jsObject2java(value);
		this.type = SQLUtil.parseSQLType(this.value);
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder(super.toString());

		ret.append(" (type: ");
		ret.append(type);
		ret.append(", value: ");
		ret.append(value);
		ret.append(")");

		return ret.toString();
	}
}

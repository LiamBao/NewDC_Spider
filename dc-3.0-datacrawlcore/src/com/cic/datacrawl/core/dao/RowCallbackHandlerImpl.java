package com.cic.datacrawl.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;

import com.cic.datacrawl.core.entity.BaseEntity;

public abstract class RowCallbackHandlerImpl implements RowCallbackHandler {
	public RowCallbackHandlerImpl() {
	}

	private List<BaseEntity> result = new ArrayList<BaseEntity>();

	private BaseEntity[] entityArray;

	public void processRow(ResultSet rs) throws SQLException {
		result.add(buildEntity(rs));
	}

	public BaseEntity[] getBaseEntities() {
		if (entityArray == null) {
			entityArray = new BaseEntity[result.size()];
			result.toArray(entityArray);
		}
		return entityArray;
	}

	public BaseEntity getBaseEntities(int i) {
		if (i < 0 || i > result.size()) {
			return null;
		}
		return result.get(i);
	}

	abstract protected BaseEntity buildEntity(ResultSet rs) throws SQLException ;
}

package com.cic.datacrawl.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cic.datacrawl.core.entity.BaseEntity;

public class EntityUtil {
	public static boolean isSameType(List<BaseEntity> entityList) {
		if (entityList == null)
			throw new NullPointerException();
		if (entityList.size() == 0)
			throw new ArrayIndexOutOfBoundsException();
		Class<? extends BaseEntity> clazz = entityList.get(0).getClass();
		for (int i = 1; i < entityList.size(); ++i) {
			if (!entityList.get(i).getClass().equals(clazz)) {
				return false;
			}
		}
		return true;
	}

	public static List<List<BaseEntity>> group(List<BaseEntity> entityList) {
		ArrayList<List<BaseEntity>> list = new ArrayList<List<BaseEntity>>();
		if (entityList == null || entityList.size() == 0)
			return list;

		HashMap<String, List<BaseEntity>> map = new HashMap<String, List<BaseEntity>>();
		for (int i = 0; i < entityList.size(); ++i) {
			List<BaseEntity> singleTypeList = null;
			if (!map.containsKey(entityList.get(i).getTheEntityName())) {
				singleTypeList = new ArrayList<BaseEntity>();
				map.put(entityList.get(i).getTheEntityName(), singleTypeList);
			}
			singleTypeList = map.get(entityList.get(i).getTheEntityName());
			singleTypeList.add(entityList.get(i));
		}

		list.addAll(map.values());
		return list;
	}

	public static boolean isEmpty(BaseEntity entity) {
		if (entity == null)
			return true;
		String[] columns = entity.getColumnNames();
		if (columns == null || columns.length == 0) {
			return true;
		}
		return false;
	}

	public static boolean isSameType(BaseEntity[] entities) {
		if (entities == null)
			throw new NullPointerException();
		if (entities.length == 0)
			throw new ArrayIndexOutOfBoundsException();

		Class<? extends BaseEntity> clazz = entities[0].getClass();
		for (int i = 1; i < entities.length; ++i) {
			if (!entities[i].getClass().equals(clazz)) {
				return false;
			}
		}
		return true;
	}
}

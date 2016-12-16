package com.cic.datacrawl.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.cic.datacrawl.core.entity.BaseEntity;

public class JSONUtil {
//	public static BaseEntity parseToEntity(String jsonString) {
//
//	}
//
//	public static Map<String, Object> parseToMap(String jsonString) {
//JSONObject jsonObject = JSONObject.fromObject(jsonString);
//jsonObject.
//	}
//
//	public static List<BaseEntity> parseToList(String jsonString) {
//
//	}

	public static String parseJSONString(List<BaseEntity> list) {
		List<String> outputList = new LinkedList<String>();
		for (int j = 0; j < list.size(); ++j) {
			if (list.get(j) instanceof BaseEntity) {
				outputList.add(((BaseEntity) list.get(j)).toJSONString());
			} else {
				outputList.add(list.get(j).toString());
			}
		}
		JSONArray array = new JSONArray();
		array.add(outputList);

		return array.toString();
	}

	public static String parseJSONString(long userId, String userName) {
		JSONObject obj = new JSONObject();
		obj.put("text", userName);
		obj.put("value", userId);

		JSONArray array = new JSONArray();
		array.add(obj);
		return array.toString();
	}

	public static String parseJSONString(Map<String, BaseEntity> map) {
		String[] mapKeys = new String[map.size()];
		map.keySet().toArray(mapKeys);
		HashMap<String, String> outputMap = new HashMap<String, String>();
		for (int j = 0; j < map.size(); ++j) {
			if (map.get(mapKeys[j]) instanceof BaseEntity) {
				outputMap.put(mapKeys[j], map.get(mapKeys[j]).toJSONString());
			} else {
				outputMap.put(mapKeys[j], map.get(mapKeys[j]).toString());
			}
		}
		JSONArray array = new JSONArray();
		array.add(outputMap);
		return array.toString();
	}

}

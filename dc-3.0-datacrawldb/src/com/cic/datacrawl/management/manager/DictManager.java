package com.cic.datacrawl.management.manager;

import com.cic.datacrawl.management.entity.Dict;
import com.cic.datacrawl.management.manager.base.DictBaseManager;

public class DictManager extends DictBaseManager {
	public String getText(String type, String value) {
		Dict dict = getDictDAO().queryByTypeValue(type, value);
		return dict == null ? value : dict.getText();
	}
}

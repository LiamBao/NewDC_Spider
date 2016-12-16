package com.cic.datacrawl.management.dao;

import com.cic.datacrawl.management.dao.base.*;

public interface AccountDAO extends AccountBaseDAO{
	int updateKey(long id, String newKey, String key);

	int invalidAccount(long siteId, String username);
	
}


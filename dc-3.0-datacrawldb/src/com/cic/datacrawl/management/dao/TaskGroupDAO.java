package com.cic.datacrawl.management.dao;


public interface TaskGroupDAO {

	void registerSpliter (final int taskGroupId, final String lanIP);
	void finishedSpliter (final int taskGroupId, final byte errorFlag, final String exception);
	
}
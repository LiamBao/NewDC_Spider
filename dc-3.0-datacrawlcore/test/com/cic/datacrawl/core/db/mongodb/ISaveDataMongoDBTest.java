package com.cic.datacrawl.core.db.mongodb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
@ContextConfiguration(locations = {	
		"/beans/core/beans_init.xml", "/beans/core/beans.database.xml",
		"/beans/core/beans_wsclient.xml", "/beans/core/beans.core.xml","/beans/core/beans_save.xml" })
public class ISaveDataMongoDBTest {

	@Autowired
	private ISaveDataMongoDB  saveDataMongoDB;
	
	/**
	 * 测试用例，MonogDB请连接192.168.7.198
	 * @throws Exception
	 */
	@Test
	public void testCount() throws Exception {
		String query = "{}";
		long res = saveDataMongoDB.count("T_SE_RESULT_201205", query);
		System.out.println(res);
		assertTrue(res >= 0);
	}
	
	@Test
	public void testIfExist() throws Exception {
		DefaultEntity entity = new DefaultEntity("T_SE_RESULT_201205");
		entity.set("_id", "f0a3e0c9f9b1fcc4edf7e917eb86189251d8ded3");
		long res = saveDataMongoDB.ifExist("T_SE_RESULT_201205", entity);
		System.out.println(res);
		assertTrue(res >= 0);
	}
	
	@Test
	public void testIfExists() throws Exception {
		DefaultEntity entity = new DefaultEntity("T_BBS_THREAD");
		entity.set("_id", "f0a3e0c9f9b1fcc4edf7e917eb86189251d8ded3");
		
		DefaultEntity entity2 = new DefaultEntity("T_BBS_THREAD");
		entity2.set("_id", "4fc354e808b08c4fc94548eb");
		
		BaseEntity[] entities = new BaseEntity[2];
		entities[0] = entity;
		entities[1] = entity2;
		
		long[] res = saveDataMongoDB.ifExist("T_BBS_THREAD", entities);
		for(int i=0;i<res.length;i++){
			System.out.println(res[i]);
		}
		assertTrue(res.length > 0);
	}
	

}

package com.cic.datacrawl.core.db;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.javascript.NativeObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;
import com.cicdata.datacollection.storeservice.beans.ws.WsTableName;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
@ContextConfiguration(locations = { "/beans/core/beans_init.xml",
		"/beans/core/beans.database.xml", "/beans/core/beans_wsclient.xml",
		"/beans/core/beans.core.xml" })
public class DbConnectionMongoDBUpdateTest {

	private DbConnectionMongoDB dbConnectionMongoDB;

	@Before
	public void onSetUp() throws Exception {
		dbConnectionMongoDB = new DbConnectionMongoDB("http://192.168.7.221:8880/dcStore/service/wsStoreService?wsdl");
	}


	@Test
	public void test() throws Exception {
		DefaultEntity entity = new DefaultEntity(WsTableName.FOO.getTableName());
		entity.set("KeyTerm", "data.KeyTerm");
		entity.set("date", new Date());
		entity.set("_id", 234);
		BaseEntity[] entityArrays = new BaseEntity[2];
		entityArrays[0] = entity;
		entityArrays[1] = entity;
		dbConnectionMongoDB.save(WsTableName.FOO.getTableName(), entityArrays);
		NativeObject no = new NativeObject();
		// no.setAttributes("name", 4);
		dbConnectionMongoDB.update(WsTableName.FOO.getTableName(), "{_id:234}",	no);
	}
	
	@Test
	public void testUpdate() throws Exception {
		DefaultEntity entity = new DefaultEntity(WsTableName.FOO.getTableName());
		entity.set("KeyTerm", "data.KeyTerm");
		entity.set("date", new Date());
		entity.set("_id", 234);
		BaseEntity[] entityArrays = new BaseEntity[2];
		entityArrays[0] = entity;
		DefaultEntity entity2 = new DefaultEntity(WsTableName.FOO.getTableName());
		entity2.set("_id", 234);
		entity2.set("ddd", new Date());
		entityArrays[1] = entity;
		dbConnectionMongoDB.save(WsTableName.FOO.getTableName(), entityArrays);
		NativeObject no = new NativeObject();
		// no.setAttributes("name", 4);
		dbConnectionMongoDB.update(WsTableName.FOO.getTableName(), "{_id:234}",	no);
	}

}

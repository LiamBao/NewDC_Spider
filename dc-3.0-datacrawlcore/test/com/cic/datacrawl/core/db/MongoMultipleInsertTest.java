package com.cic.datacrawl.core.db;

import java.util.Calendar;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;


/**
 * 分表存储插入测试
 * @author tyler.yan
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
@ContextConfiguration(locations = {	
		"/beans/core/beans_init.xml", "/beans/core/beans.database.xml",
		"/beans/core/beans_wsclient.xml", "/beans/core/beans.core.xml" })
public class MongoMultipleInsertTest {

	private DbConnectionMongoDB dbConnectionMongoDB;
	
	@Before
	public void onSetUp() throws Exception {
		dbConnectionMongoDB = new DbConnectionMongoDB("http://192.168.7.221:8880/dcStore/service/wsStoreService?wsdl");
	}

	@Test
	@Repeat(3)
	public void testPOSTSave() throws Exception {
		DefaultEntity entity = new DefaultEntity("T_BBS_POST");
		entity.set("dateofpost", new Date());
		entity.set("date", new Date());
		BaseEntity[] entityArrays = new BaseEntity[6];
		entityArrays[0] = entity;
		entityArrays[1] = entity;
		DefaultEntity e2 = new DefaultEntity("T_BBS_POST");
		Calendar c = Calendar.getInstance();
		c.set(1990, 2, 3);
		e2.set("dateofpost", c.getTime());
		entityArrays[2] = e2;
		DefaultEntity e3 = new DefaultEntity("T_BBS_POST");
		c.set(1991, 2, 3);
		e3.set("dateofpost", c.getTime());
		entityArrays[3] = e3;
		DefaultEntity e4 = new DefaultEntity("T_BBS_POST");
		c.set(1992, 2, 3);
		e4.set("dateofpost", c.getTime());
		entityArrays[4] = e4;
		DefaultEntity e5 = new DefaultEntity("T_BBS_POST");
		c.set(1993, 2, 3);
		e5.set("dateofpost", c.getTime());
		entityArrays[5] = e5;
		dbConnectionMongoDB.save("T_BBS_POST", entityArrays);
	}
	
	@Test
	@Repeat(3)
	public void testSERASave() throws Exception {
		DefaultEntity entity = new DefaultEntity("T_SE_RESULT");
		entity.set("dateofresult", new Date());
		entity.set("date", new Date());
		BaseEntity[] entityArrays = new BaseEntity[6];
		entityArrays[0] = entity;
		entityArrays[1] = entity;
		DefaultEntity e2 = new DefaultEntity("T_SE_RESULT");
		Calendar c = Calendar.getInstance();
		c.set(1800, 2, 3);
		e2.set("dateofresult",c.getTime());
		entityArrays[2] = e2;
		DefaultEntity e3 = new DefaultEntity("T_SE_RESULT");
		c.set(1801, 2, 3);
		e3.set("dateofresult",c.getTime());
		entityArrays[3] = e3;
		DefaultEntity e4 = new DefaultEntity("T_SE_RESULT");
		c.set(1802, 2, 3);
		e4.set("dateofresult",c.getTime());
		entityArrays[4] = e4;
		DefaultEntity e5 = new DefaultEntity("T_SE_RESULT");
		c.set(1803, 2, 3);
		e5.set("dateofresult",c.getTime());
		entityArrays[5] = e5;
		dbConnectionMongoDB.save("T_SE_RESULT", entityArrays);
	}
	


}

package com.cic.datacrawl.core.db;

import java.util.Date;

import org.apache.log4j.Logger;
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
import com.cicdata.datacollection.storeservice.beans.ws.WsTableName;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
@ContextConfiguration(locations = {	
		"/beans/core/beans_init.xml", "/beans/core/beans.database.xml",
		"/beans/core/beans_wsclient.xml", "/beans/core/beans.core.xml" })
public class DbConnectionMongoDBTest {

	
	private static Logger log = Logger.getLogger(DbConnectionMongoDBTest.class);
	private DbConnectionMongoDB dbConnectionMongoDB;
	
	@Before
	public void onSetUp() throws Exception {
		dbConnectionMongoDB = new DbConnectionMongoDB("http://192.168.7.221:8880/dcStore/service/wsStoreService?wsdl");
	}

	@Test
	@Repeat(3)
	public void testSave() throws Exception {
		DefaultEntity entity = new DefaultEntity("T_PROJECT_SE_KEYWORD_VIEW");
		entity.set("KeyTerm", "data.KeyTerm");
		entity.set("Keyword", "data.Keyword");
		entity.set("ScrapePages", "data.ScrapePages");
		entity.set("ProjectId", "data.ProjectId");
		entity.set("SearchEngineId", "data.SearchEngineId");
		entity.set("SearchEngineUrl", "data.SearchEngineUrl");
		entity.set("ItemType", "data.ItemType");
		entity.set("IsAvailable", "data.IsAvailable");
		entity.set("IsRankByRel", "data.IsRankByRel");
		entity.set("IsRankByTime", "data.IsRankByTime");
		entity.set("IsInSite", "data.IsInSite");
		entity.set("IsInUrl", "data.IsInUrl");
		entity.set("InUrl", "data.InUrl");
		entity.set("SERA_Type", "data.SERA_Type");
		entity.set("Last_split_time", "data.Last_split_time");
		entity.set("ProjectId", "data.ProjectId");
		entity.set("SEID", "data.SEID");
		entity.set("DomainId", "data.DomainId");
		entity.set("Domain", "data.Domain");
		entity.set("ItemType", "data.ItemType");
		entity.set("date", new Date());
		BaseEntity[] entityArrays = new BaseEntity[2];
		entityArrays[0] = entity;
		entityArrays[1] = entity;
		dbConnectionMongoDB.save("foo", entityArrays);
	}
	
	@Test
	@Repeat(1)
	public void testSelectMongodb() {
		log.info("before test select mongodb.");
		BaseEntity[] res;
		try {
			
			String q1 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[{'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15:50:14')}}], 'sera_type':0}";
			String q2 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[{'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15:50')}}], 'sera_type':0}";
			String q3 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[{'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15')}}], 'sera_type':0}";
			String q4 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[{'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25')}}], 'sera_type':0}";
			res = dbConnectionMongoDB.query(WsTableName.FOO.getTableName(), q1, 0, 20, "{_id:-1}");
			for(int i = 0;i<res.length;i++){
				System.out.println("q1:" + res.toString());
			}
			res = dbConnectionMongoDB.query(WsTableName.FOO.getTableName(), q2, 0, 20, "{_id:-1}");
			for(int i = 0;i<res.length;i++){
				System.out.println("q1:" + res.toString());
			}
			res = dbConnectionMongoDB.query(WsTableName.FOO.getTableName(), q3, 0, 20, "{_id:-1}");
			for(int i = 0;i<res.length;i++){
				System.out.println("q1:" + res.toString());
			}
			res = dbConnectionMongoDB.query(WsTableName.FOO.getTableName(), q4, 0, 20, "{_id:-1}");
			for(int i = 0;i<res.length;i++){
				System.out.println("q1:" + res.toString());
			}
			
			String q = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[{'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15:50:14.617')}}], 'sera_type':0}";
			log.info("dbConnectionMongoDB-->" + dbConnectionMongoDB);
			res = dbConnectionMongoDB.query(WsTableName.FOO.getTableName(), q, 0, 20, "{_id:-1}");
			for(int i = 0;i<res.length;i++){
				System.out.println(res.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("end test select mongodb.");
	}
	
	@Test
	@Repeat(1)
	public void testCountMongodb() {
		log.info("before test count;");
		String q1 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[ {'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15:50:14.617')}}], 'sera_type':0}";
		String q2 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[ {'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15:50:14')}}], 'sera_type':0}";
		String q3 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[ {'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15:50')}}], 'sera_type':0}";
		String q4 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[ {'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15')}}], 'sera_type':0}";
		String q5 = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[ {'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25')}}], 'sera_type':0}";
		long res;
		try {
			res = dbConnectionMongoDB.count(WsTableName.FOO.getTableName(), q1);
			System.out.println("q1:" + res);
			res = dbConnectionMongoDB.count(WsTableName.FOO.getTableName(), q2);
			System.out.println("q2:" + res);
			res = dbConnectionMongoDB.count(WsTableName.FOO.getTableName(), q3);
			System.out.println("q3:" + res);
			res = dbConnectionMongoDB.count(WsTableName.FOO.getTableName(), q4);
			System.out.println("q4:" + res);
			res = dbConnectionMongoDB.count(WsTableName.FOO.getTableName(), q5);
			System.out.println("q5:" + res);
			
			log.info("dbConnectionMongoDB-->" + dbConnectionMongoDB);
			res = dbConnectionMongoDB.count(WsTableName.FOO.getTableName(), "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[ {'last_split_time':null}, {'last_split_time':{'$lt':new Date('2014/04/25 15:50:14.617')}}], 'sera_type':0}");
			System.out.println("count:" + res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("end test count");
	}

}

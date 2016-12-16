package com.cic.datacrawl.core.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SimpleTimeZone;

import net.sf.json.JSONObject;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;
import org.mozilla.javascript.NativeObjectUtil;
import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;
import com.cic.datacrawl.core.util.ArrayUtil;
import com.cic.datacrawl.core.util.ErrorInfo;
import com.cic.datacrawl.core.util.ErrorUtils;
import com.cic.datacrawl.core.util.StringUtil;
import com.cicdata.datacollection.storeservice.beans.ws.UpdateBean;
import com.cicdata.datacollection.storeservice.beans.ws.WsDataCountRQ;
import com.cicdata.datacollection.storeservice.beans.ws.WsDataCountRS;
import com.cicdata.datacollection.storeservice.beans.ws.WsDataInsertRQ;
import com.cicdata.datacollection.storeservice.beans.ws.WsDataInsertRS;
import com.cicdata.datacollection.storeservice.beans.ws.WsDataQueryRQ;
import com.cicdata.datacollection.storeservice.beans.ws.WsDataQueryRS;
import com.cicdata.datacollection.storeservice.beans.ws.WsDataUpdateRQ;
import com.cicdata.datacollection.storeservice.beans.ws.WsDataUpdateRS;
import com.cicdata.datacollection.storeservice.beans.ws.WsInsertType;
import com.cicdata.datacollection.storeservice.beans.ws.WsTableName;
import com.cicdata.datacollection.storeservice.ws.IDCStoreService;

public class DbConnectionMongoDB {
	protected static Logger LOG = Logger.getLogger(DbConnectionMongoDB.class);
	private IDCStoreService wsStoreClient;
//	static {
//		try {
//			ResourceBundle rb = ResourceBundle
//					.getBundle("beans/core/dc_conf_dts_dev");
//			String address = rb.getString("store.ws.url");
//			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
//			factory.setServiceClass(IDCStoreService.class);
//			factory.setAddress(address);
//			wsStoreClient = (IDCStoreService) factory.create();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public DbConnectionMongoDB(String address) {
		try {
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setServiceClass(IDCStoreService.class);
			factory.setAddress(address);
			wsStoreClient = (IDCStoreService) factory.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 调用WEBServiceAPI接口实现Query数据
	 * 
	 * @param tableName
	 *            数据库表名
	 * @param query
	 *            查询字符串
	 * @param maxResult
	 *            最大返回结果数
	 * @param sort
	 *            排序条件
	 * @return
	 * @throws Exception
	 */
	public BaseEntity[] query(String tableName, String query, int maxResult,
			String sort) throws Exception {
		return query(tableName, query, 0, maxResult, sort);
	}

	private String replaceDatePrefix(String str) {
		str = StringUtil.replaceAll(str, "new Date(", "");
		str = StringUtil.replaceAll(str, ")", "");
		return str;
	}

	/**
	 * 调用WEBServiceAPI接口实现Query数据
	 * 
	 * @param tableName
	 *            数据库表名
	 * @param fristResult
	 *            第一条结果位置(计数从0开始)
	 * @param queryString
	 *            查询字符串
	 * @param maxResult
	 *            最大返回结果数
	 * @param sortStr
	 *            排序条件
	 * @return
	 * @throws Exception
	 */
	public BaseEntity[] query(String tableName, String queryString,
			int fristResult, int maxResult, String sortStr) throws Exception {
		queryString = formatMangoDBDate(queryString);
		LOG.info("send query info t:" + tableName + " q:" + queryString + " f:"
				+ fristResult + " x:" + maxResult + " s:" + sortStr);
		queryString = replaceDatePrefix(queryString);
		WsDataQueryRQ wsDataQueryRQ = new WsDataQueryRQ();
		wsDataQueryRQ.setTableName(new WsTableName(tableName));
		wsDataQueryRQ.setQueryStr(queryString);
		wsDataQueryRQ.setSortStr(sortStr);
		wsDataQueryRQ.setMaxResult(maxResult);
		wsDataQueryRQ.setFirstResult(fristResult);
		WsDataQueryRS selectMongodb = wsStoreClient
				.selectMongodb(wsDataQueryRQ);
		if (selectMongodb.isSuccess()) {
			List<String> results = selectMongodb.getResults();
			if (results == null) {
				LOG.info("invoke dcStore api success ,got return data is null");
				return new BaseEntity[0];
			}
			LOG.info("invoke dcStore api success ,got return data size is "
					+ results.size());
			BaseEntity[] be = new BaseEntity[results.size()];
			for (int i = 0; i < results.size(); i++) {
				String json = results.get(i);
				BaseEntity baseEntity = new DefaultEntity(tableName);
				JSONObject jsonObj = JSONObject.fromObject(json);
				Iterator<?> keys = jsonObj.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					baseEntity.set(key, jsonObj.get(key));
				}
				be[i] = baseEntity;
			}
			LOG.info("BaseEntity size is "
					+ be.length);
			return be;

		} else {
			throw new RuntimeException(selectMongodb.getErrorCode() + " : "
					+ selectMongodb.getErrorDesc());
		}
	}

	public long count(String tableName, String queryString) throws Exception {
		queryString = formatMangoDBDate(queryString);
		queryString = replaceDatePrefix(queryString);
		WsDataCountRQ wsDataCountRQ = new WsDataCountRQ();
		wsDataCountRQ.setTableName(new WsTableName(tableName));
		wsDataCountRQ.setQueryStr(queryString);
		WsDataCountRS countMongodb = wsStoreClient.countMongodb(wsDataCountRQ);

		if (countMongodb.isSuccess()) {
			return countMongodb.getCount();
		} else {
			return -1;
		}
	}
	
	public long ifExist(String tableName, BaseEntity entity) throws Exception {
		WsDataCountRQ wsDataCountRQ = new WsDataCountRQ();
		wsDataCountRQ.setTableName(new WsTableName(tableName));
		wsDataCountRQ.setQueryStr(entity.toJSONString(false));
		WsDataCountRS countMongodb = wsStoreClient.countMongodb(wsDataCountRQ);
		if (countMongodb.isSuccess()) {
			return countMongodb.getCount();
		} else {
			return -1;
		}
		
	}

	/**
	 * 
	 * @param tableName
	 * @param entities
	 * @param ifTE
	 *            是否入TE库
	 * @throws Exception
	 */
	public void save(String tableName, BaseEntity[] entities, boolean ifTE)
			throws Exception {
		if (ArrayUtil.isEmpty(entities))
			return;
		LOG.info("save " + entities.length + " rows data to " + tableName);
		try {
			WsDataInsertRQ wsDataInsertRQ = new WsDataInsertRQ();
			wsDataInsertRQ.setTableName(new WsTableName(tableName));
			wsDataInsertRQ.setIfTE(ifTE);
			List<String> dataList = new ArrayList<String>();
			for (BaseEntity entity : entities) {
				dataList.add(entity.toMongoJSONString());
			}
			wsDataInsertRQ.setType(WsInsertType.JSON);
			wsDataInsertRQ.setDataList(dataList);
			WsDataInsertRS results = wsStoreClient.saveMongodb(wsDataInsertRQ);
			if (results.isSuccess()) {
				LOG.info("invoke dcStore api success ");
			} else {
				LOG.error("invoke dcStore save data failed![code:"
						+ results.getErrorCode() + "][desc:"
						+ results.getErrorDesc() + "]");
				ErrorInfo errorInfo = new ErrorInfo();
				errorInfo.setErrorCode(results.getErrorCode());
				errorInfo.setErrorMsg(results.getErrorDesc());
				errorInfo.setErrorIds(results.getErrorList());
				ErrorUtils.reportError(errorInfo);
				throw new Exception(results.getErrorDesc());
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void save(String tableName, BaseEntity[] entities) throws Exception {
		this.save(tableName, entities, false);
	}

	/**
	 * 
	 * @param tableName
	 * @param array
	 *            ：JS传递过来的NativeArray
	 * @throws Exception
	 */
	public void save(String tableName, Object array) throws Exception {
		BaseEntity[] baseEntities = jsObject2BaseEntity(array);
		if (ArrayUtil.isNotEmpty(baseEntities)) {
			save(tableName, baseEntities);
		}
	}

	/**
	 * 
	 * @param tableName
	 * @param array
	 *            ：JS传递过来的NativeArray
	 * @throws Exception
	 */
	public void insert(String tableName, Object array) throws Exception {
		BaseEntity[] baseEntities = jsObject2BaseEntity(array);
		if (ArrayUtil.isNotEmpty(baseEntities)) {
			insert(tableName, baseEntities);
		}
	}

	public void insert(String tableName, BaseEntity[] entities)
			throws Exception {
		this.insert(tableName, entities, false);
	}

	/**
	 * 
	 * @param tableName
	 * @param entities
	 * @param ifTE
	 *            是否入TE库
	 * @throws Exception
	 */
	public void insert(String tableName, BaseEntity[] entities, boolean ifTE)
			throws Exception {
		if (ArrayUtil.isEmpty(entities))
			return;
		LOG.info("save " + entities.length + " rows data to " + tableName);
		try {
			WsDataInsertRQ wsDataInsertRQ = new WsDataInsertRQ();
			wsDataInsertRQ.setTableName(new WsTableName(tableName));
			wsDataInsertRQ.setIfTE(ifTE);
			List<String> dataList = new ArrayList<String>();
			for (BaseEntity entity : entities) {
				String temStr = entity.toMongoJSONString();
				dataList.add(temStr);
//				LOG.info(temStr);
			}
			wsDataInsertRQ.setType(WsInsertType.JSON);
			wsDataInsertRQ.setDataList(dataList);
			WsDataInsertRS results = wsStoreClient
					.insertMongodb(wsDataInsertRQ);
			if (results.isSuccess()) {
				LOG.info("invoke dcStore api success ");
			} else {
				LOG.error("invoke dcStore save data failed![code:"
						+ results.getErrorCode() + "][desc:"
						+ results.getErrorDesc() + "]");
				ErrorInfo errorInfo = new ErrorInfo();
				errorInfo.setErrorCode(results.getErrorCode());
				errorInfo.setErrorMsg(results.getErrorDesc());
				errorInfo.setErrorIds(results.getErrorList());
				ErrorUtils.reportError(errorInfo);
				throw new Exception(results.getErrorDesc());
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param tableName
	 *            表名
	 * @param query
	 *            查询条件
	 * @param update
	 *            更新条件
	 * @param upsert
	 *            是否插入
	 * @param mult
	 *            是否批量更新
	 *            update(WsTableName.T_BBS_FORUM.getTableName(),"{_id:'123'}"
	 *            ,"{$set:{name:'tyler'}}",false,false)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void update(String tableName, String query, Object update,
			boolean upsert, boolean mult) throws Exception {

		query = formatMangoDBDate(query);

		List<BaseEntity> data = new ArrayList<BaseEntity>();

		if (update instanceof org.mozilla.javascript.IdScriptableObject) {
			data = (List<BaseEntity>) NativeObjectUtil.jsObject2java(update);
		}
		UpdateBean b = new UpdateBean();
		b.setWhereSql(query);
		JSONObject jo = new JSONObject();
		for (BaseEntity entry : data) {
			String[] columnNames = entry.getColumnNames();
			for (String column : columnNames) {
				jo.put(column, entry.get(column));
			}
		}
		// 更新数据要求是标准的JSON字符串
		b.setSetSql(jo.toString());
		List<UpdateBean> beans = new ArrayList<UpdateBean>();
		beans.add(b);
		this.updateMongodb(tableName, beans);
	}

	private BaseEntity[] jsObject2BaseEntity(Object array) {
		BaseEntity[] baseEntities = null;
		Object obj = NativeObjectUtil.jsObject2java(array);
		if (!(obj instanceof List)) {
			if (obj instanceof BaseEntity) {
				baseEntities = new BaseEntity[] { (BaseEntity) obj };
			}
		} else {
			List<?> objs = (List<?>) obj;
			if (objs.size() > 0) {
				baseEntities = new BaseEntity[objs.size()];
				for (int i = 0; i < objs.size(); ++i) {
					baseEntities[i] = (BaseEntity) objs.get(i);
				}
			}

		}
		return baseEntities;
	}

	public static void main(String[] args) {
		String testString = "{'isinsite':1, 'searchengineid':20013, 'isavailable':1, '$or':[ {'last_split_time':null}, {'last_split_time':{'$lt':new Date(\"2012/05/09 08:50:14.617\")}}], 'sera_type':1}{'$lt':new Date('2012/05/09 08:50:14.617')}}]{'$lt':new Date(\"2012/05/09 08:50:14.617\")}}]";
		System.out.println(testString);
		System.out.println("    "+formatMangoDBDate(testString));
	}

	private static String formatMangoDBDate(String queryStr) {
		String[] splitChars = new String[] { "'", "\"" };
		for (int m = 0; m < splitChars.length; ++m) {
			String[] str = queryStr.split(splitChars[m]);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < str.length; ++i) {
				if (i > 0)
					sb.append("'");

				if (isDateString(str[i])) {
					sb.append(formatSingleDate(str[i]));
				} else {
					sb.append(str[i]);
				}
			}

			queryStr = sb.toString();
		}
		return queryStr;
	}

	private static boolean isDateString(String str) {
		return parseDate(str) != null;

	}

	private static Date parseDate(String str) {
		String[] formater = new String[] { "yyyy/MM/dd HH:mm:ss.SSS",
				"yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM/dd" };
		Date ret = null;
		for (int i = 0; ret == null && i < formater.length; ++i) {
			try {
				ret = new SimpleDateFormat(formater[i]).parse(str);
			} catch (Exception e) {
			}
		}
		return ret;
	}

	private static String formatSingleDate(String dateStr) {
		Date d = parseDate(dateStr);
		Calendar c = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
		c.setTimeInMillis(d.getTime());
		c.add(Calendar.HOUR_OF_DAY, -8);
		d.setTime(c.getTimeInMillis());
		String ret = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(d);

		return ret;
	}

	/**
	 * 
	 * @param tableName
	 * @param query
	 * @param update
	 * @param upsert
	 * @throws Exception
	 *             update(WsTableName.T_BBS_FORUM.getTableName(),"{_id:'123'}",
	 *             "{$set:{name:'tyler'}}",false)
	 */
	public void update(String tableName, String query, Object update,
			boolean upsert) throws Exception {

		this.update(tableName, query, update, upsert, false);
	}

	/**
	 * 更新mongodb数据
	 * 
	 * @param tableName
	 *            : mongodb数据库表名
	 * @param query
	 *            :monogdb json查询字符串
	 * @param update
	 *            :mongodb update对象 JS 调用override有问题注意修改方法名
	 *            update(WsTableName.T_BBS_FORUM
	 *            .getTableName(),"{_id:'123'}","{$set:{name:'tyler'}}")
	 * @throws Exception
	 */
	public void update(String tableName, String query, Object update)
			throws Exception {

		this.update(tableName, query, update, false);
	}

	public int[] updateMongodb(String tableName, List<UpdateBean> beans)
			throws Exception {
		if (beans == null || beans.size() == 0) {
			LOG.info("update beans size is 0, return ");
			return null;
		}
		try {
			WsDataUpdateRQ wsDataUpdateRQ = new WsDataUpdateRQ();
			wsDataUpdateRQ.setTableName(new WsTableName(tableName));
			wsDataUpdateRQ.setDataList(beans);
			WsDataUpdateRS results = wsStoreClient
					.updateMongodb(wsDataUpdateRQ);
			if (results.isSuccess()) {
				LOG.debug("invoke dcStore api success for update!");
				return results.getResults();
			} else {
				LOG.error("save data failed! send error info........................."
						+ results.getErrorCode()
						+ " - "
						+ results.getErrorDesc());
				ErrorInfo errorInfo = new ErrorInfo();
				errorInfo.setErrorCode(results.getErrorCode());
				errorInfo.setErrorMsg(results.getErrorDesc());
				errorInfo.setErrorIds(results.getErrorList());
				ErrorUtils.reportError(errorInfo);
				throw new Exception(results.getErrorDesc());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			ErrorInfo errorInfo = new ErrorInfo();
			errorInfo.setErrorCode("DTS_00001");
			errorInfo.setErrorMsg(e.getMessage());
			StringBuilder sb = new StringBuilder();
			for (UpdateBean b : beans) {
				sb.append(b.getWhereSql());
				sb.append(",");
			}
			errorInfo.setErrorIds(sb.toString());
			ErrorUtils.reportError(errorInfo);
			throw e;
		}
	}


}

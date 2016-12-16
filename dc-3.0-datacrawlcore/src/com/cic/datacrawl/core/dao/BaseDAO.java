package com.cic.datacrawl.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.EntityUtil;
import com.cic.datacrawl.core.util.StringUtil;

public abstract class BaseDAO extends JdbcDaoSupport {
	
	private static final Logger log = Logger.getLogger(BaseDAO.class);
	
	public void save(BaseEntity entity) {
		if (update(entity) == 0) {
			insert(entity);
		}
	}

	public int insert(BaseEntity entity) {
		String sql = buildInsertSQL(entity);
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql,
				new PreparedStatementSetterImpl(entity));
	}

	public int[] insert(BaseEntity[] entities) {
		int[] ret = new int[0];
		if (entities != null && entities.length > 0) {
			if (EntityUtil.isSameType(entities)) {
				String sql = buildInsertSQL(entities[0]);
				log.info("SQL:"+ sql);
				return getJdbcTemplate().batchUpdate(sql,
						new BatchPreparedStatementSetterImpl(entities));
			} else {
				ArrayList<Integer> list = new ArrayList<Integer>();
				// TODO 可以将同类型的entity合并提交，但是会导致影响行数和数据无法对应
				for (int i = 0; i < entities.length; ++i) {
					list.add(new Integer(insert(entities[i])));
				}
				ret = new int[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					ret[i] = list.get(i).intValue();
				}
			}
		}
		return ret;
	}

	public int[] insert(List<BaseEntity> entityList) {
		int[] ret = new int[0];
		if (entityList != null && entityList.size() > 0) {
			BaseEntity[] entities = new BaseEntity[entityList.size()];
			entityList.toArray(entities);
			ret = insert(entities);
		}
		return ret;
	}

	public int[] update(BaseEntity[] entities,
			Map<String, Object> conditionMap, String conditionString) {

		int[] ret = new int[0];
		if (entities != null && entities.length > 0) {
			if (EntityUtil.isSameType(entities)) {
				String sql = buildUpdateSQL(entities[0], conditionMap,
						conditionString);
				log.info("SQL:"+ sql);
				return getJdbcTemplate().batchUpdate(
						sql,
						new BatchPreparedStatementSetterImpl(entities,
								conditionMap));
			} else {
				ArrayList<Integer> list = new ArrayList<Integer>();
				// TODO 可以将同类型的entity合并提交，但是会导致影响行数和数据无法对应
				for (int i = 0; i < entities.length; ++i) {
					Map<String, Object> theConditionMap = conditionMap;
					if (conditionMap == null) {
						theConditionMap = entities[i].getConditionMap();
					} else {
						theConditionMap = conditionMap;
					}
					list.add(new Integer(update(entities[i], theConditionMap,
							conditionString)));
				}
				ret = new int[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					ret[i] = list.get(i).intValue();
				}
			}
		}
		return ret;
	}

	public int update(BaseEntity entity, Map<String, Object> conditionMap,
			String conditionString) {

		String sql = buildUpdateSQL(entity, conditionMap, conditionString);
		if (StringUtil.isEmpty(sql))
			return 0;
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql,
				new PreparedStatementSetterImpl(entity, conditionMap));
	}

	protected String buildUpdateSQL(BaseEntity entity,
			Map<String, Object> conditionMap, String conditionString) {

		StringBuffer ret = new StringBuffer();
		if (entity.getValueMap().size() > 0) {
			ret.append("UPDATE ");
			ret.append(entity.getTheEntityName());
			ret.append(" SET ");
			Set<String> keySet = conditionMap.keySet();
			String[] conditionColumns = new String[keySet.size()];
			keySet.toArray(conditionColumns);
			for (int i = 0; i < conditionColumns.length; ++i) {
				if (i > 0)
					ret.append(", ");
				ret.append(conditionColumns[i]);
				ret.append("=?");
			}
			ret.append(buildWhereString(conditionMap, conditionString));
		}
		return ret.toString();
	}

	public int update(BaseEntity entity, String conditionString) {
		return update(entity, entity.getConditionMap(), conditionString);
	}

	public int update(BaseEntity entity) {
		return update(entity, entity.getConditionMap(), null);
	}

	public int[] update(BaseEntity[] entities, String conditionString) {
		return update(entities, null, conditionString);
	}

	public int[] update(List<BaseEntity> entityList, String conditionString) {
		if (entityList == null || entityList.size() == 0)
			return new int[0];
		BaseEntity[] entities = new BaseEntity[entityList.size()];
		return update(entities, conditionString);
	}

	public int update(BaseEntity entity, Map<String, Object> conditionMap) {
		return update(entity, conditionMap, null);
	}

	public int[] update(BaseEntity[] entities, Map<String, Object> conditionMap) {
		return update(entities, conditionMap, null);
	}

	public int[] update(List<BaseEntity> entityList,
			Map<String, Object> conditionMap) {
		if (entityList == null || entityList.size() == 0)
			return new int[0];
		BaseEntity[] entities = new BaseEntity[entityList.size()];
		return update(entities, conditionMap);
	}

	public BaseEntity[] select(
			RowCallbackHandlerImpl processor, String sqlString) {
		log.info("SQL:"+ sqlString);
		getJdbcTemplate().query(sqlString, processor);
		return processor.getBaseEntities();
	}

	public BaseEntity[] select(BaseEntity entity) {
		String sql = buildSelectSQL(entity, null);

		RowCallbackHandlerImpl processor = new RowCallbackHandlerImpl() {
			@Override
			protected BaseEntity buildEntity(ResultSet rs)throws SQLException {
				return createEntity(rs);
			}
		};
		log.info("SQL:"+ sql);
		getJdbcTemplate().query(sql, processor);
		return processor.getBaseEntities();
	}

	public BaseEntity[] select(BaseEntity entity, String conditionString) {
		String sql = buildSelectSQL(entity, conditionString);

		RowCallbackHandlerImpl processor = new RowCallbackHandlerImpl() {
			@Override
			protected BaseEntity buildEntity(ResultSet rs) throws SQLException {
				return createEntity(rs);
			}
		};
		if (entity.getValueMap().size() > 0) {
			log.info("SQL:"+ sql);
			getJdbcTemplate().query(sql,
					new PreparedStatementSetterImpl(entity), processor);
		} else {
			log.info("SQL:"+ sql);
			getJdbcTemplate().query(sql, processor);
		}
		return processor.getBaseEntities();
	}

	public int delete(BaseEntity entity) {
		String sql = buildDeleteSQL(entity, null);
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql,
				new PreparedStatementSetterImpl(entity));

	}

	public int delete(BaseEntity entity, String conditionString) {
		String sql = buildDeleteSQL(entity, conditionString);
		log.info("SQL:"+ sql);
		return getJdbcTemplate().update(sql,
				new PreparedStatementSetterImpl(entity));
	}

	public int count(BaseEntity entity) {
		String sql = "select count(*) from " + entity.getTheEntityName();
		log.info("SQL:"+ sql);
		return getJdbcTemplate().queryForInt(sql);
	}

	protected String[] buildDeleteSQL(BaseEntity[] entities,
			String conditionString) {
		LinkedList<String> sqlList = new LinkedList<String>();
		for (int i = 0; i < entities.length; ++i) {
			BaseEntity entity = entities[i];
			if (entity != null) {
				sqlList.add(buildDeleteSQL(entity, conditionString));
			}
		}
		String[] ret = new String[sqlList.size()];
		sqlList.toArray(ret);
		return ret;
	}

	protected String[] buildInsertSQL(BaseEntity[] entities) {
		LinkedList<String> sqlList = new LinkedList<String>();
		for (int i = 0; i < entities.length; ++i) {
			BaseEntity entity = entities[i];
			if (entity != null) {
				sqlList.add(buildInsertSQL(entity));
			}
		}
		String[] ret = new String[sqlList.size()];
		sqlList.toArray(ret);
		return ret;
	}

	protected String buildInsertSQL(BaseEntity entity) {
		if (EntityUtil.isEmpty(entity))
			throw new NullPointerException();
		String[] columns = entity.getColumnNames();
		StringBuffer ret = new StringBuffer();
		ret.append("INSERT INTO ");
		ret.append(entity.getTheEntityName());
		ret.append(" (");
		for (int i = 0; i < columns.length; ++i) {
			if (i > 0)
				ret.append(", ");
			ret.append(columns[i]);
		}
		ret.append(") VALUES (");
		for (int i = 0; i < columns.length; ++i) {
			if (i > 0)
				ret.append(", ?");
			else
				ret.append("?");
		}
		ret.append(")");
		return ret.toString();
	}

	protected String buildWhereString(Map<String, Object> conditionMap,
			String conditionString) {
		if ((conditionMap == null || conditionMap.size() == 0)
				&& (conditionString == null || conditionString.trim().length() == 0)) {
			return "";
		}
		boolean appendWhere = false;
		StringBuffer ret = new StringBuffer();
		if (conditionMap.size() > 0) {
			conditionString = conditionString.trim();

			ret.append(" WHERE ");
			appendWhere = true;
			Set<String> keySet = conditionMap.keySet();
			String[] conditionColumns = new String[keySet.size()];
			keySet.toArray(conditionColumns);
			for (int i = 0; i < conditionColumns.length; ++i) {
				if (i > 0)
					ret.append(" AND ");
				ret.append(conditionColumns[i]);
				ret.append("=?");
			}
		}
		if (conditionString != null) {
			boolean needAppendWhere = false;
			if (conditionString.toLowerCase().startsWith("where")) {
				conditionString = conditionString.substring(5).trim();
				needAppendWhere = true;
			}
			if (conditionString.length() > 0) {
				if (needAppendWhere && !appendWhere) {
					ret.append(" WHERE");
				} else if (appendWhere && needAppendWhere) {
					ret.append(" AND");
				}
				ret.append(" ");
				ret.append(conditionString);
			}
		}
		return ret.toString();
	}

	protected String buildDeleteSQL(BaseEntity entity, String conditionString) {
		if (EntityUtil.isEmpty(entity))
			throw new NullPointerException();

		StringBuffer ret = new StringBuffer();
		ret.append("DELETE FROM ");
		entity.getTheEntityName();
		ret.append(buildWhereString(entity.getValueMap(), conditionString));
		return ret.toString();
	}

	protected String buildSelectSQL(BaseEntity entity, String conditionString) {
		if (EntityUtil.isEmpty(entity))
			throw new NullPointerException();
		String[] columns = entity.getColumnNames();
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT ");
		for (int i = 0; i < columns.length; ++i) {
			if (i > 0)
				ret.append(", ");
			ret.append(columns[i]);
		}
		ret.append(" FROM ");
		entity.getTheEntityName();
		ret.append(buildWhereString(entity.getValueMap(), conditionString));
		return ret.toString();
	}

	abstract protected BaseEntity createEntity(ResultSet rs) throws SQLException;
}

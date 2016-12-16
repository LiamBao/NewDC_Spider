package com.cic.datacrawl.core.jsfunction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeObjectUtil;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;
import com.cic.datacrawl.core.entity.EntitySaveManager;

/**
 * 支持Rhino所需的为数据实体操作提供的全局系统函数，
 */
public final class RhinoEntityFunction {

	public static void commit(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		try {
			((EntitySaveManager) ApplicationContext.getInstance().getBean("saveManager")).commit();
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			throw new RuntimeException(e);
		}
	}

	public static void setBufferedSize(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		int bufferedSize = EntitySaveManager.DEFUALT_BUFFERED_SIZE;
		try {
			bufferedSize = Integer.parseInt(ObjectUtils.toString(args[0]));
		} catch (Throwable e) {
			bufferedSize = EntitySaveManager.DEFUALT_BUFFERED_SIZE;
		}

		if (bufferedSize < 0) {
			bufferedSize = EntitySaveManager.DEFUALT_BUFFERED_SIZE;
		}

		((EntitySaveManager) ApplicationContext.getInstance().getBean("saveManager"))
				.setBufferSize(bufferedSize);
	}

	public static void clean(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		((EntitySaveManager) ApplicationContext.getInstance().getBean("saveManager")).clean();
	}

	public static void saveWithoutValidation(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length != 2) {
			JavaScriptException error = new JavaScriptException("Illegal Argument in save(name, object)", "",
					0);
			throw error;
		}

		String name = ObjectUtils.toString(args[0]);
		BaseEntity[] entities = null;
		if (args[1] instanceof NativeObject) {
			NativeObject jsObject = (NativeObject) args[1];
			BaseEntity entity = convertToEntity(name, jsObject, cx, thisObj, funObj);
			if (entity != null) {
				entities = new BaseEntity[1];
				entities[0] = entity;
			}
		} else if (args[1] instanceof NativeArray) {
			ArrayList<BaseEntity> entityList = new ArrayList<BaseEntity>();
			NativeArray array = (NativeArray) args[1];
			for (int i = 0; i < array.getLength(); ++i) {
				Object obj = array.get(i, array);
				if (obj instanceof BaseEntity) {
					entityList.add((BaseEntity) obj);
				} else if (obj instanceof NativeObject) {
					entityList.add(convertToEntity(name, (NativeObject) obj, cx, thisObj, funObj));
				}
			}
			entities = new BaseEntity[entityList.size()];
			entityList.toArray(entities);
		} else if (args[1] instanceof NativeJavaObject) {
			Object obj = ((NativeJavaObject) args[1]).unwrap();

			if (obj instanceof BaseEntity) {
				entities = new BaseEntity[1];
				entities[0] = (BaseEntity) obj;

			} else if (obj instanceof BaseEntity[]) {
				entities = (BaseEntity[]) obj;

			} else if (obj instanceof List<?>) {
				ArrayList<BaseEntity> entityList = new ArrayList<BaseEntity>();
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) obj;
				for (int i = 0; i < list.size(); ++i) {
					Object object = list.get(i);
					if (object instanceof BaseEntity) {
						entityList.add((BaseEntity) object);
					}
				}
				entities = new BaseEntity[entityList.size()];
				entityList.toArray(entities);
			}

		} else if (args[1] instanceof BaseEntity) {
			entities = new BaseEntity[1];
			entities[0] = (BaseEntity) args[1];

		} else if (args[1] instanceof BaseEntity[]) {
			entities = (BaseEntity[]) args[1];

		} else if (args[1] instanceof List) {
			ArrayList<BaseEntity> entityList = new ArrayList<BaseEntity>();
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) args[1];
			for (int i = 0; i < list.size(); ++i) {
				Object obj = list.get(i);
				if (obj instanceof BaseEntity) {
					entityList.add((BaseEntity) obj);
				}
			}
			entities = new BaseEntity[entityList.size()];
			entityList.toArray(entities);
		}
		if (entities != null) {
			EntitySaveManager saveManager = (EntitySaveManager) ApplicationContext.getInstance()
					.getBean("saveManager");

			saveManager.save(entities);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	private static BaseEntity convertToEntity(String name, NativeObject jsObject, Context cx,
			Scriptable thisObj, Function funObj) {

		BaseEntity entity = new DefaultEntity(name);
		Object[] ids = jsObject.getIds();
		for (int i = 0; i < ids.length; ++i) {
			String key = ObjectUtils.toString(ids[i]);
			Object obj = NativeObjectUtil.jsObject2java(jsObject.get(key, jsObject));
			entity.set(key, obj);

			if (obj instanceof Date) {
				try {
					String dateFormat = ObjectUtils.toString(cx
							.evaluateString(thisObj, "objectModuleDefine."
														+ name
														+ "."
														+ key
														+ ".dateFormatDefine", "", 0, null));

					entity.setDateFormat(key, dateFormat);
				} catch (Exception e) {

				}
			}
		}
		return entity;
	}
}

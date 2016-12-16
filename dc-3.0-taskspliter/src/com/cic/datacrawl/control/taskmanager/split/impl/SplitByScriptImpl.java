package com.cic.datacrawl.control.taskmanager.split.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import org.apache.log4j.Logger;
import org.mozilla.javascript.NativeObjectUtil;
import com.cic.datacrawl.control.taskmanager.split.Spliter;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.initialize.RunOnStartup;
import com.cic.datacrawl.core.initialize.RunOnStartupExecutor;
import com.cic.datacrawl.core.jsfunction.RhinoStandardFunction;
import com.cic.datacrawl.core.rhino.RhinoContext;
import com.cic.datacrawl.core.rhino.shell.Global;
import com.cic.datacrawl.core.util.FileUtils;
import com.cic.datacrawl.management.entity.SubTaskEntity;
import com.cic.datacrawl.management.entity.Task;

public class SplitByScriptImpl extends Spliter {
	
	private static final Logger LOG = Logger.getLogger(SplitByScriptImpl.class);
	private static ScriptRunner runner;

	public static final String FUNCTION_SPLIT = "doSplit();";
	public static final String FUNCTION_COUNT = "doCount();";
	public static final String FUNCTION_UPDATE = "doUpdateTaskSource();";

	private static ScriptRunner getScriptRunner() throws Exception {
		if (runner == null)
			runner = new ScriptRunner();
		return runner;
	}

	private boolean needExecute(Task task) throws Exception {
		if (task == null) {
			LOG.info("task is null");
			return false;
		}
		// RhinoContextGlobalRegister.getInstance().registParameter("define",defines);
		getScriptRunner().setValue("task", task);
//		getScriptRunner().setValue("STATUS_PADDING", SubTaskManager.STATUS_PADDING);
//		getScriptRunner().setValue("STATUS_ERROR", SubTaskManager.STATUS_ERROR);
//		getScriptRunner().setValue("STATUS_FINISHED", SubTaskManager.STATUS_FINISHED);
//		getScriptRunner().setValue("STATUS_RUNNING", SubTaskManager.STATUS_RUNNING);
		getScriptRunner().setValue("PAGE_SIZE", getPageSize());
		getScriptRunner().setValue("SPLIT_START_TIME", new Timestamp(currentTime));
		File scriptFile = new File(task.getSplitFile());
		if (!scriptFile.exists()) {
			LOG.info("scriptFile path :" + scriptFile.getAbsolutePath());
			String errorMessage = "Script file is not exist.(AbsolutePath: " + scriptFile.getAbsolutePath() + ",\tFilepath: "
					+ task.getSplitFile() + ")";
			throw new Exception(errorMessage);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected SubTaskEntity[] convert(Task task) throws Exception {
		Object o = null;
		if (needExecute(task)) {
			File scriptFile = new File(task.getSplitFile());
			LOG.info("Execute Task(" + task.getId() + "): " + scriptFile.getAbsoluteFile() + "." + FUNCTION_SPLIT);

			Config.setJSFolder(FileUtils.getParentAbsolutePath(scriptFile));
			// 启动脚本
			try {
				o = getScriptRunner().startup(scriptFile, FUNCTION_SPLIT);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				throw new Exception(e.getMessage());
			}
		}
		o = NativeObjectUtil.jsObject2java(o);
		if (o == null || !(o instanceof List<?>)) {
			return new SubTaskEntity[0];
		}
		List<SubTaskEntity> list = (List<SubTaskEntity>) o;
		SubTaskEntity[] ret = new SubTaskEntity[list.size()];
		list.toArray(ret);

		return ret;
	}

	@Override
	protected int count(Task task) throws Exception {
		Object o = null;
		if (needExecute(task)) {
			File scriptFile = new File(task.getSplitFile());
			LOG.info("Execute Task(" + task.getId() + "): " + scriptFile.getAbsoluteFile() + "." + FUNCTION_COUNT);
			Config.setJSFolder(FileUtils.getParentAbsolutePath(scriptFile));
			// 启动脚本
			try {
				o = getScriptRunner().startup(scriptFile, FUNCTION_COUNT);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				throw new Exception(e.getMessage());
			}
		}else{
			LOG.info("not need execute");
		}
		o = NativeObjectUtil.jsObject2java(o);
		if (o == null || !(o instanceof Number)) {
			return 0;
		} else {
			return ((Number) o).intValue();
		}
	}

	@Override
	protected void updateTaskSource(Task task) throws Exception {
		if (needExecute(task)) {
			File scriptFile = new File(task.getSplitFile());
			LOG.info("Execute Task(" + task.getId() + "): " + scriptFile.getAbsoluteFile() + "." + FUNCTION_UPDATE);
			Config.setJSFolder(FileUtils.getParentAbsolutePath(scriptFile));
			// 启动脚本
			try {
				getScriptRunner().startup(scriptFile, FUNCTION_UPDATE);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				throw new Exception(e.getMessage());
			}
		}
	}

}

class ScriptRunner {
	private final Logger LOG = Logger.getLogger(ScriptRunner.class);
	private boolean needInit = true;
	private RhinoContext rhinoContext;

	public void setValue(String name, Object value) {
		if (name == null || name.trim().length() == 0 || value == null)
			return;
		rhinoContext.putJavaObject(name, value, value.getClass());
	}

	public ScriptRunner() throws Exception {
		init();
	}

	/**
	 * johnney.bu 注释掉152、153、156三行初始化browser的逻辑
	 * @throws Exception
	 */
	private void init() throws Exception {
		if (needInit) {
//			JavaWebBrowserImpl javaBrowser = new BackgroundBrowser().getWebBrowser();
//			RhinoBrowserImpl browser = RhinoBrowserImpl.newInstance(javaBrowser);
			rhinoContext = new RhinoContext(Global.getInstance());
			RhinoStandardFunction.rhinoContext = rhinoContext;
//			rhinoContext.putJavaObject("browser", browser, RhinoBrowser.class);
			final RhinoContext finalRhinoContext = rhinoContext;
			((RunOnStartup) ApplicationContext.getInstance().getBean("runOnStartup")).execute(new RunOnStartupExecutor() {

				@Override
				public void execute(String scriptPath) throws Exception {
					String script = FileUtils.readFile(scriptPath, "UTF-8");
					finalRhinoContext.execute(script);
				}
			});
			needInit = false;
		}
	}

	public Object execute(String executeJSString) throws Exception {
		Object ret = null;
		if (executeJSString != null) {
			executeJSString = executeJSString.trim();
			if (executeJSString.length() > 0) {
				ret = rhinoContext.evaluate(executeJSString);
			}
		}
		return ret;
	}

	public Object startup(File scriptFile, String executeJSString) throws Exception {
		String scriptBody = FileUtils.readFile(scriptFile, "UTF-8");
		try {
			if (scriptBody != null) {
				scriptBody = scriptBody.trim();
				if (scriptBody.length() > 0) {
					rhinoContext.execute(scriptBody, scriptFile.getAbsolutePath());
				}
			}
			if (executeJSString != null) {
				executeJSString = executeJSString.trim();
				if (executeJSString.length() > 0) {
					return rhinoContext.evaluate(executeJSString);
				}
			}
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
		}
		return null;
	}
}

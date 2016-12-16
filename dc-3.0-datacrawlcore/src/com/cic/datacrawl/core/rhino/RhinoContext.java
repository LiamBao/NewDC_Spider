package com.cic.datacrawl.core.rhino;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.cic.datacrawl.core.browser.RhinoBrowser;
import com.cic.datacrawl.core.jsfunction.RhinoStandardFunction;
import com.cic.datacrawl.core.rhino.shell.Global;
import com.cic.datacrawl.core.util.FileUtils;

/**
 * Rhino 脚本引擎
 */
public class RhinoContext {
	private static final Log logger = LogFactory.getLog(RhinoContext.class);

	private void initialGlobalParameters() {
		GlobalParameterDefination[] parameters = RhinoContextGlobalRegister
				.getInstance().getAllParameterDefinations();
		if (parameters != null) {
			for (int i = 0; i < parameters.length; ++i) {
				putJavaObject(parameters[i].getName(), parameters[i]
						.getInstance());
			}
		}
	}

	private void initialGlobalFunctions() {
		GlobalFunctionDefination[] functions = RhinoContextGlobalRegister
				.getInstance().getAllFunctionDefinations();
		if (functions != null) {
			for (int i = 0; i < functions.length; ++i) {
				rhinoScope.defineFunctionProperties(functions[i].getNames(),
						functions[i].getClazz(), functions[i].getAttributes());
			}
		}
	}

	public RhinoContext() {
		this(new ImporterTopLevel(Context.enter(), false));
	}

	public RhinoContext(Scriptable scope) {
		ImporterTopLevel obj = new ImporterTopLevel(Context.enter(), false);
		obj.setParentScope(scope);
		init(obj);
	}

	public RhinoContext(Global obj) {
		init(obj);
	}

	public RhinoContext(ScriptableObject scriptableObject) {
		init(scriptableObject);
	}

	private void init(ScriptableObject scriptableObject) {
		this.rhinoScope = scriptableObject;
		initialGlobalFunctions();
		initialGlobalParameters();
	}

	public void setURL(String url) {
		putJavaObject("url", url);
	}

	public void setHTMLContent(String htmlContent) {
		putJavaObject("htmlContent", htmlContent);
	}

	public static RhinoContext createForCrawl() {
		Context cx = Context.enter();
		try {
			RhinoContext rhinoContext = new RhinoContext(cx
					.initStandardObjects());
			rhinoContext.rhinoScope = Global.getInstance();
			// rhinoContext.putJavaObject("browser", rhinoBrowser);
			// Object wrappedOut = new
			// NativeJavaInterface(rhinoContext.rhinoScope, rhinoBrowser,
			// RhinoBrowser.class);
			// ScriptableObject.putProperty(rhinoContext.rhinoScope, "browser",
			// wrappedOut);
			return rhinoContext;
		} finally {
			Context.exit();
		}
	}

	public void setBrowser(RhinoBrowser rhinoBrowser) {
		browser = rhinoBrowser;
		putJavaObject("browser", rhinoBrowser);
	}

	private RhinoBrowser browser;

	/**
	 * @return the browser
	 */
	public RhinoBrowser getBrowser() {
		return browser;
	}

	private ScriptableObject rhinoScope;

	public ScriptableObject getRhinoScope() {
		return rhinoScope;
	}

	// public void putJavaObject(String varName, Object javaObject) {
	// // putJavaObject(varName, javaObject, javaObject.getClass());
	// if (!(javaObject instanceof Scriptable)) {
	// javaObject = new NativeJavaObject(this.rhinoScope, javaObject,
	// javaObject.getClass());
	// javaObject = Context.javaToJS(javaObject, rhinoScope);
	// }
	// ScriptableObject.putProperty(rhinoScope, varName, javaObject);
	// }
	//
	// public void putJavaObject(String varName, Object javaObject,
	// Class<?> javaInterfaceClass) {
	//
	// if (!(javaObject instanceof Scriptable)) {
	// javaObject = new NativeJavaInterface(this.rhinoScope, javaObject,
	// javaInterfaceClass);
	// javaObject = Context.javaToJS(javaObject, rhinoScope);
	// }
	// ScriptableObject.putProperty(rhinoScope, varName, javaObject);
	// }

	public void complie(String filename) {
		String fileContent = null;
		try {
			fileContent = FileUtils.readFile(filename, "UTF-8");
		} catch (IOException e) {
		}
		if (fileContent != null) {
			Context cx = Context.enter();
			try {
				cx.compileString(fileContent, filename, 1, null);
			} finally {
				Context.exit();
			}
		}
	}

	/**
	 * 执行脚本
	 * 
	 * @param script
	 */
	public void execute(String script, String fileUrl) {
		Context cx = Context.enter();
		long startTime = System.currentTimeMillis();
		RhinoStandardFunction.resetTotalSleepTime();
		logger.info("Starting execute file: "+ fileUrl);
		try {
			
			Object result = cx.evaluateString(rhinoScope, script, fileUrl, 1, null);
			if(logger.isDebugEnabled())
				logger.debug("执行返回的结果："+result.toString());
			
		}catch(Exception e){
			
			logger.error("Execute the execute file error: "+e.getMessage(), e);
			//e.printStackTrace();
		
		}finally{
			
			long realExecuteTime = System.currentTimeMillis() - startTime;
			logger.info("Execute Finished. Total execute time: "
					+ realExecuteTime + "ms. Total sleep time: "
					+ RhinoStandardFunction.getTotalSleepTime()
					+ "ms. Real execute time: "
					+ (realExecuteTime - RhinoStandardFunction.getTotalSleepTime())
					+ "ms.");
			Context.exit();
		}
	}

	/**
	 * 执行脚本
	 * 
	 * @param script
	 */
	public void execute(String script) {
		execute(script, "<cmd>");
	}
	
	//执行某个文件中的函数，add by steven；
	public void executeMain(String script,String filename){
		complie(filename);
		execute(script);
	}

	/**
	 * 执行脚本并获取返回结果
	 * 
	 * @param script
	 * @return
	 */
	public Object evaluate(String script, String fileUrl) {
		Context cx = Context.enter();
		try {
			Object result = cx.evaluateString(rhinoScope, script, fileUrl, 1,
					null);
			if (result instanceof NativeJavaObject)
				return ((NativeJavaObject) result).unwrap();
			else
				return result;
		} finally {
			Context.exit();
		}
	}

	/**
	 * 执行脚本并获取返回结果
	 * 
	 * @param script
	 * @return
	 */
	public Object evaluate(String script) {
		return evaluate(script, "<cmd>");
	}

	/**
	 * 调用javascript中定义的函数,并获取返回结果
	 * 
	 * @param jsFunctionName
	 * @param functionArgs
	 * @return
	 */
	public Object call(String jsFunctionName, Object... functionArgs) {
		Context cx = Context.enter();
		try {
			Object fObj = rhinoScope.get(jsFunctionName, rhinoScope);
			if (fObj == null)
				throw new RuntimeException("Function " + jsFunctionName
						+ " not defined.");
			if (!(fObj instanceof Function))
				throw new RuntimeException("Object " + jsFunctionName
						+ " isn't a function.");

			Function f = (Function) fObj;
			Object result = f.call(cx, rhinoScope, rhinoScope, functionArgs);
			return result;
		} finally {
			Context.exit();
		}
	}

	/* 读取文本文件 */

	/* 读取文本文件 */
	public static void writeTextFile(String content, String fileName) {
		try {
			writeTextFile(content, new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/* 读取文本内容 */
	public static void writeTextFile(String content, OutputStream stream) {
		try {
			stream.write(content.getBytes("UTF-8"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}
	}

	public void putJavaObject(String varName, Object javaObject) {
		rhinoScope.defineProperty(varName, javaObject,
				ScriptableObject.DONTENUM);
	}

	public void putJavaObject(String varName, Object javaObject,
			Class<?> javaInterfaceClass) {
		Object wrappedOut = new NativeJavaObject(this.rhinoScope,
				javaObject, javaInterfaceClass);
		putJavaObject(varName, wrappedOut);
	}

}

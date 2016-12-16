/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino JavaScript Debugger code, released
 * November 21, 2000.
 *
 * The Initial Developer of the Original Code is
 * SeeBeyond Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2000
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Igor Bukanov
 *   Matt Gould
 *   Christopher Oliver
 *   Cameron McCormack
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.cic.datacrawl.core.rhino.debugger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SecurityUtilities;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableObject;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.initialize.RunOnStartupExecutor;
import com.cic.datacrawl.core.jsfunction.RhinoStandardFunction;
import com.cic.datacrawl.core.rhino.shell.Global;
import com.cic.datacrawl.core.util.FileUtils;

/**
 * Dim or Debugger Implementation for Rhino.
 */
public class RhinoDim implements RunOnStartupExecutor {

	private static final Logger logger = Logger.getLogger(RhinoDim.class);

	// Constants for instructing the debugger what action to perform
	// to end interruption. Used by 'returnValue'.
	public static final int STEP_OVER = 0;
	public static final int STEP_INTO = 1;
	public static final int STEP_OUT = 2;
	public static final int GO = 3;
	public static final int BREAK = 4;
	public static final int EXIT = 5;

	// Constants for the DimIProxy interface implementation class.
	private static final int IPROXY_DEBUG = 0;
	private static final int IPROXY_LISTEN = 1;
	private static final int IPROXY_COMPILE_SCRIPT = 2;
	private static final int IPROXY_EVAL_SCRIPT = 3;
	private static final int IPROXY_STRING_IS_COMPILABLE = 4;
	private static final int IPROXY_OBJECT_TO_STRING = 5;
	private static final int IPROXY_OBJECT_PROPERTY = 6;
	private static final int IPROXY_OBJECT_IDS = 7;

	/**
	 * Interface to the debugger GUI.
	 */
	private GuiCallback callback;

	/**
	 * Whether the debugger should break.
	 */
	private boolean breakFlag;

	/**
	 * The ScopeProvider object that provides the scope in which to evaluate
	 * script.
	 */
	private ScopeProvider scopeProvider;

	/**
	 * The index of the current stack frame.
	 */
	private int frameIndex = -1;

	/**
	 * Information about the current stack at the point of interruption.
	 */
	private volatile ContextData interruptedContextData;

	/**
	 * The ContextFactory to listen to for debugging information.
	 */
	private ContextFactory contextFactory;

	/**
	 * Synchronization object used to allow script evaluations to happen when a
	 * thread is resumed.
	 */
	private Object monitor = new Object();

	/**
	 * Synchronization object used to wait for valid
	 * {@link #interruptedContextData}.
	 */
	private Object eventThreadMonitor = new Object();

	/**
	 * The action to perform to end the interruption loop.
	 */
	private volatile int returnValue = -1;

	/**
	 * Whether the debugger is inside the interruption loop.
	 */
	private volatile boolean insideInterruptLoop;

	/**
	 * The requested script string to be evaluated when the thread has been
	 * resumed.
	 */
	private String evalRequest;

	/**
	 * The stack frame in which to evaluate {@link #evalRequest}.
	 */
	private StackFrame evalFrame;

	/**
	 * The result of evaluating {@link #evalRequest}.
	 */
	private Object evalResult;
	/**
	 * The result of evaluating {@link #evalRequest}.
	 */
	private String evalResultString;

	/**
	 * Whether the debugger should break when a script exception is thrown.
	 */
	private boolean breakOnExceptions;

	/**
	 * Whether the debugger should break when a script function is entered.
	 */
	private boolean breakOnEnter;

	/**
	 * Whether the debugger should break when a script function is returned
	 * from.
	 */
	private boolean breakOnReturn;

	/**
	 * Table mapping URLs to information about the script source.
	 */
	private final Map<String, SourceInfo> urlToSourceInfo = Collections
			.synchronizedMap(new HashMap<String, SourceInfo>());

	/**
	 * Table mapping function names to information about the function.
	 */
	private final Map<String, FunctionSource> functionNames = Collections
			.synchronizedMap(new HashMap<String, FunctionSource>());

	/**
	 * Table mapping functions to information about the function.
	 */
	private final Map<DebuggableScript, FunctionSource> functionToSource = Collections
			.synchronizedMap(new HashMap<DebuggableScript, FunctionSource>());

	/**
	 * ContextFactory.Listener instance attached to {@link #contextFactory}.
	 */
	private DimIProxy listener;

	/**
	 * Sets the GuiCallback object to use.
	 */
	public void setGuiCallback(GuiCallback callback) {
		this.callback = callback;
	}

	/**
	 * Tells the debugger to break at the next opportunity.
	 */
	public void setBreak() {
		this.breakFlag = true;
	}

	/**
	 * Sets the ScopeProvider to be used.
	 */
	public void setScopeProvider(ScopeProvider scopeProvider) {
		this.scopeProvider = scopeProvider;
	}

	/**
	 * Switches context to the stack frame with the given index.
	 */
	public void contextSwitch(int frameIndex) {
		this.frameIndex = frameIndex;
	}

	/**
	 * Sets whether the debugger should break on exceptions.
	 */
	public void setBreakOnExceptions(boolean breakOnExceptions) {
		this.breakOnExceptions = breakOnExceptions;
	}

	/**
	 * Sets whether the debugger should break on function entering.
	 */
	public void setBreakOnEnter(boolean breakOnEnter) {
		this.breakOnEnter = breakOnEnter;
	}

	/**
	 * Sets whether the debugger should break on function return.
	 */
	public void setBreakOnReturn(boolean breakOnReturn) {
		this.breakOnReturn = breakOnReturn;
	}

	/**
	 * Attaches the debugger to the given ContextFactory.
	 */
	public void attachTo(ContextFactory factory) {
		detach();
		this.contextFactory = factory;
		this.listener = new DimIProxy(this, IPROXY_LISTEN);
		factory.addListener(this.listener);
	}

	/**
	 * Detaches the debugger from the current ContextFactory.
	 */
	public void detach() {
		if (listener != null) {
			contextFactory.removeListener(listener);
			contextFactory = null;
			listener = null;
		}
	}

	/**
	 * Releases resources associated with this debugger.
	 */
	public void dispose() {
		detach();
	}

	/**
	 * Returns the FunctionSource object for the given script or function.
	 */
	private FunctionSource getFunctionSource(DebuggableScript fnOrScript) {
		FunctionSource fsource = functionSource(fnOrScript);
		if (fsource == null) {
			String url = getNormalizedUrl(fnOrScript);
			SourceInfo si = sourceInfo(url);
			if (si == null) {
				if (!fnOrScript.isGeneratedScript()) {
					// Not eval or Function, try to load it from URL
					String source = loadSource(url);
					if (source != null) {
						DebuggableScript top = fnOrScript;
						for (;;) {
							DebuggableScript parent = top.getParent();
							if (parent == null) {
								break;
							}
							top = parent;
						}
						registerTopScript(top, source);
						fsource = functionSource(fnOrScript);
					}
				}
			}
		}
		return fsource;
	}

	/**
	 * Loads the script at the given URL.
	 */
	private String loadSource(String sourceUrl) {
		String source = null;
		int hash = sourceUrl.indexOf('#');
		if (hash >= 0) {
			sourceUrl = sourceUrl.substring(0, hash);
		}
		try {
			InputStream is;
			openStream: {
				if (sourceUrl.indexOf(':') < 0) {
					// Can be a file name
					try {
						if (sourceUrl.startsWith("~/")) {
							String home = SecurityUtilities.getSystemProperty("user.home");
							if (home != null) {
								String pathFromHome = sourceUrl.substring(2);
								File f = new File(new File(home), pathFromHome);
								if (f.exists()) {
									is = new FileInputStream(f);
									break openStream;
								}
							}
						}
						File f = new File(sourceUrl);
						if (f.exists()) {
							is = new FileInputStream(f);
							break openStream;
						}
					} catch (SecurityException ex) {
					}
					// No existing file, assume missed http://
					if (sourceUrl.startsWith("//")) {
						sourceUrl = "http:" + sourceUrl;
					} else if (sourceUrl.startsWith("/")) {
						sourceUrl = "http://127.0.0.1" + sourceUrl;
					} else if (sourceUrl.trim().length() > 0) {
						sourceUrl = "http://" + sourceUrl;
					}
				}

				is = (new URL(sourceUrl)).openStream();
			}

			try {
				source = Kit.readReader(new InputStreamReader(is));
			} finally {
				is.close();
			}
		} catch (IOException ex) {
			System.err.println("Failed to load source from " + sourceUrl + ": " + ex);
		}
		return source;
	}

	/**
	 * Registers the given script as a top-level script in the debugger.
	 */
	private void registerTopScript(DebuggableScript topScript, String source) {
		if (!topScript.isTopLevel()) {
			throw new IllegalArgumentException();
		}
		String url = getNormalizedUrl(topScript);
		DebuggableScript[] functions = getAllFunctions(topScript);
		final SourceInfo sourceInfo = new SourceInfo(source, functions, url);

		synchronized (urlToSourceInfo) {
			SourceInfo old = urlToSourceInfo.get(url);
			if (old != null) {
				sourceInfo.copyBreakpointsFrom(old);
			}
			urlToSourceInfo.put(url, sourceInfo);
			/*
			 * Iterator<String> keyIterator = functionNames.keySet().iterator();
			 * synchronized (functionToSource) { while (keyIterator.hasNext()) {
			 * String key = keyIterator.next(); if (key.startsWith(url)) {
			 * FunctionSource fsource = functionNames.get(key); if
			 * (functionToSource.containsValue(fsource)) {
			 * Iterator<DebuggableScript> ki = functionToSource
			 * .keySet().iterator(); while (ki.hasNext()) { DebuggableScript
			 * keyScript = ki.next(); FunctionSource o = functionToSource
			 * .get(keyScript); if (fsource.equals(o)) {
			 * functionToSource.remove(keyScript); } } }
			 * functionNames.remove(key); } } }
			 */
			for (int i = 0; i != sourceInfo.functionSourcesTop(); ++i) {
				FunctionSource fsource = sourceInfo.functionSource(i);
				String name = fsource.name();
				if (name.length() - sourceInfo.url().length() - 1 > 0) {
					functionNames.put(name, fsource);
				}
			}
		}

		synchronized (functionToSource) {
			for (int i = 0; i != functions.length; ++i) {
				FunctionSource fsource = sourceInfo.functionSource(i);
				functionToSource.put(functions[i], fsource);
			}
		}

		callback.updateSourceText(sourceInfo);
	}

	/**
	 * Returns the FunctionSource object for the given function or script.
	 */
	private FunctionSource functionSource(DebuggableScript fnOrScript) {
		return functionToSource.get(fnOrScript);
	}

	/**
	 * Returns an array of all function names.
	 */
	public String[] functionNames() {
		synchronized (urlToSourceInfo) {
			return functionNames.keySet().toArray(new String[functionNames.size()]);
		}
	}

	/**
	 * Returns the FunctionSource object for the function with the given name.
	 * 
	 * @throws Exception
	 */
	public FunctionSource functionSourceByName(String functionName) throws Exception {
		FunctionSource ret = functionNames.get(functionName);
		if (ret == null) {
			ArrayList<String> functionNameList = new ArrayList<String>();
			Iterator<String> keyIterator = functionNames.keySet().iterator();
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				String functionNameKey = key.substring(key.lastIndexOf(".") + 1);
				if (functionNameKey.equals(functionName)) {
					functionNameList.add(key);
				}
			}
			if (functionNameList.size() == 0) {
				return null;
			} else if (functionNameList.size() == 1) {
				return functionNames.get(functionNameList.get(0));
			} else {
				Exception e = new Exception(functionName);
				throw e;
			}
		}
		return ret;
	}

	/**
	 * Returns the SourceInfo object for the given URL.
	 */
	public SourceInfo sourceInfo(String url) {
		return urlToSourceInfo.get(url);
	}

	/**
	 * Returns the SourceInfo object for the given URL.
	 */
	public void removeSourceInfo(String url) {
		while (urlToSourceInfo.containsKey(url)) {
			SourceInfo sourceInfo = sourceInfo(url);
			for (int i = 0; i < sourceInfo.functionSourcesTop(); ++i) {
				FunctionSource function = sourceInfo.functionSource(i);
				while (functionNames.containsKey(function.name())) {
					functionNames.remove(function.name());
				}
			}
			urlToSourceInfo.remove(url);
		}
	}

	/**
	 * Returns the source URL for the given script or function.
	 */
	private String getNormalizedUrl(DebuggableScript fnOrScript) {
		String url = fnOrScript.getSourceName();
		if (url == null) {
			url = "<stdin>";
		} else {
			// Not to produce window for eval from different lines,
			// strip line numbers, i.e. replace all #[0-9]+\(eval\) by
			// (eval)
			// Option: similar teatment for Function?
			char evalSeparator = '#';
			StringBuffer sb = null;
			int urlLength = url.length();
			int cursor = 0;
			for (;;) {
				int searchStart = url.indexOf(evalSeparator, cursor);
				if (searchStart < 0) {
					break;
				}
				String replace = null;
				int i = searchStart + 1;
				while (i != urlLength) {
					int c = url.charAt(i);
					if (!('0' <= c && c <= '9')) {
						break;
					}
					++i;
				}
				if (i != searchStart + 1) {
					// i points after #[0-9]+
					if ("(eval)".regionMatches(0, url, i, 6)) {
						cursor = i + 6;
						replace = "(eval)";
					}
				}
				if (replace == null) {
					break;
				}
				if (sb == null) {
					sb = new StringBuffer();
					sb.append(url.substring(0, searchStart));
				}
				sb.append(replace);
			}
			if (sb != null) {
				if (cursor != urlLength) {
					sb.append(url.substring(cursor));
				}
				url = sb.toString();
			}
		}
		return url;
	}

	/**
	 * Returns an array of all functions in the given script.
	 */
	private static DebuggableScript[] getAllFunctions(DebuggableScript function) {
		ObjArray functions = new ObjArray();
		collectFunctions_r(function, functions);
		DebuggableScript[] result = new DebuggableScript[functions.size()];
		functions.toArray(result);
		return result;
	}

	/**
	 * Helper function for {@link #getAllFunctions(DebuggableScript)}.
	 */
	private static void collectFunctions_r(DebuggableScript function, ObjArray array) {
		array.add(function);
		for (int i = 0; i != function.getFunctionCount(); ++i) {
			collectFunctions_r(function.getFunction(i), array);
		}
	}

	/**
	 * Clears all breakpoints.
	 */
	public void clearAllBreakpoints() {
		for (SourceInfo si : urlToSourceInfo.values()) {
			si.removeAllBreakpoints();
		}
	}

	/**
	 * Called when a breakpoint has been hit.
	 */
	private void handleBreakpointHit(StackFrame frame, Context cx) {
		breakFlag = false;
		try {
			interrupted(cx, frame, null, false);
		} catch (Exception e) {
			// TODO Handle exception on interrupted.
			// throw new RuntimeException(e);
		}
	}

	private void handelExit(StackFrame frame, Context cx) {
		// TODO
	}

	/**
	 * Called when a script exception has been thrown.
	 */
	private void handleExceptionThrown(Context cx, Throwable ex, StackFrame frame) {
		if (breakOnExceptions) {
			ContextData cd = frame.contextData();
			if (cd.lastProcessedException != ex) {
				try {
					interrupted(cx, frame, ex, true);
					cd.lastProcessedException = ex;
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Returns the current ContextData object.
	 */
	public ContextData currentContextData() {
		return interruptedContextData;
	}

	/**
	 * Sets the action to perform to end interruption.
	 */
	public void setReturnValue(int returnValue) {
		synchronized (monitor) {
			this.returnValue = returnValue;
			monitor.notify();
		}
	}

	/**
	 * Resumes execution of script.
	 */
	public void go() {
		synchronized (monitor) {
			this.returnValue = GO;
			callback.resumeInterrupt();
			monitor.notifyAll();
		}
	}

	/**
	 * Evaluates the given script.
	 * 
	 * @throws Exception
	 */
	public Object evalReturnObject(String expr, boolean needException) throws Exception {
		return evalReturnObject(expr, 0, needException);
	}

	/**
	 * Evaluates the given script.
	 * 
	 * @param lineIndex
	 * @throws Exception
	 */
	public Object evalReturnObject(String expr, int lineIndex, ContextData contextData, boolean needException)
			throws Exception {
		Object result = null;
		if (expr == null) {
			return result;
		}
		if (contextData == null) {
			contextData = currentContextData();
		}
		if (contextData == null || frameIndex >= contextData.frameCount()) {
			Context cx = Context.enter();
			try {
				result = doEvalReturnObject(cx, null, expr, lineIndex, needException);
			} finally {
				if (cx != null)
					cx.exit();
			}
		} else {
			StackFrame frame = contextData.getFrame(frameIndex);
			if (contextData.eventThreadFlag) {
				Context cx = Context.getCurrentContext();
				result = doEvalReturnObject(cx, frame, expr, lineIndex, needException);
			} else {
				synchronized (monitor) {
					if (insideInterruptLoop) {
						evalRequest = expr;
						evalFrame = frame;
						monitor.notify();
						do {
							try {
								monitor.wait();
							} catch (InterruptedException exc) {
								Thread.currentThread().interrupt();
								break;
							}
						} while (evalRequest != null);
						result = evalResult;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Evaluates the given script.
	 * 
	 * @param lineIndex
	 * @throws Exception
	 */
	public Object evalReturnObject(String expr, int lineIndex, boolean needException) throws Exception {
		return evalReturnObject(expr, lineIndex, currentContextData(), needException);
	}

	/**
	 * Evaluates the given script.
	 * 
	 * @throws Exception
	 */
	public String eval(String expr, int lineIndex, boolean needException) throws Exception {
		String result = "undefined";
		if (expr == null) {
			return result;
		}
		ContextData contextData = currentContextData();
		if (contextData == null || frameIndex >= contextData.frameCount()) {
			Context cx = Context.enter();
			try {
				result = doEval(cx, null, expr, lineIndex, needException);
			} finally {
				if (cx != null)
					cx.exit();
			}
		} else {
			StackFrame frame = contextData.getFrame(frameIndex);
			if (contextData.eventThreadFlag) {
				Context cx = Context.getCurrentContext();
				result = doEval(cx, frame, expr, lineIndex, needException);
			} else {
				synchronized (monitor) {
					if (insideInterruptLoop) {
						evalRequest = expr;
						evalFrame = frame;
						monitor.notify();
						do {
							try {
								monitor.wait();
							} catch (InterruptedException exc) {
								Thread.currentThread().interrupt();
								break;
							}
						} while (evalRequest != null);
						result = evalResultString;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Evaluates the given script.
	 * 
	 * @throws Exception
	 */
	public String eval(String expr, boolean needException) throws Exception {
		return eval(expr, 0, needException);
	}

	/**
	 * Compiles the given script.
	 */
	public void compileScript(String url, String text) {
		DimIProxy action = new DimIProxy(this, IPROXY_COMPILE_SCRIPT);
		action.url = url;
		action.text = text;
		action.withContext();
	}

	/**
	 * Evaluates the given script.
	 */
	public void evalScript(final String url, final String text) {
		DimIProxy action = new DimIProxy(this, IPROXY_EVAL_SCRIPT);
		action.url = url;
		action.text = text;
		action.withContext();
	}

	/**
	 * Converts the given script object to a string.
	 */
	public String objectToString(Object object) {
		DimIProxy action = new DimIProxy(this, IPROXY_OBJECT_TO_STRING);
		action.object = object;
		action.withContext();
		return action.stringResult;
	}

	/**
	 * Returns whether the given string is syntactically valid script.
	 */
	public boolean stringIsCompilableUnit(String str) {
		DimIProxy action = new DimIProxy(this, IPROXY_STRING_IS_COMPILABLE);
		action.text = str;
		action.withContext();
		return action.booleanResult;
	}

	/**
	 * Returns the value of a property on the given script object.
	 */
	public Object getObjectProperty(Object object, Object id) {
		DimIProxy action = new DimIProxy(this, IPROXY_OBJECT_PROPERTY);
		action.object = object;
		action.id = id;
		action.withContext();
		return action.objectResult;
	}

	/**
	 * Returns an array of the property names on the given script object.
	 */
	public Object[] getObjectIds(Object object) {
		DimIProxy action = new DimIProxy(this, IPROXY_OBJECT_IDS);
		action.object = object;
		action.withContext();
		return action.objectArrayResult;
	}

	/**
	 * Returns the value of a property on the given script object.
	 */
	private Object getObjectPropertyImpl(Context cx, Object object, Object id) {
		Scriptable scriptable = (Scriptable) object;
		Object result;
		if (id instanceof String) {
			String name = (String) id;
			if (name.equals("this")) {
				result = scriptable;
			} else if (name.equals("__proto__")) {
				result = scriptable.getPrototype();
			} else if (name.equals("__parent__")) {
				result = scriptable.getParentScope();
			} else {
				result = ScriptableObject.getProperty(scriptable, name);
				if (result == ScriptableObject.NOT_FOUND) {
					result = Undefined.instance;
				}
			}
		} else {
			int index = ((Integer) id).intValue();
			result = ScriptableObject.getProperty(scriptable, index);
			if (result == ScriptableObject.NOT_FOUND) {
				result = Undefined.instance;
			}
		}
		return result;
	}

	/**
	 * Returns an array of the property names on the given script object.
	 */
	private Object[] getObjectIdsImpl(Context cx, Object object) {
		if (!(object instanceof Scriptable) || object == Undefined.instance) {
			return Context.emptyArgs;
		}

		Object[] ids;
		Scriptable scriptable = (Scriptable) object;
		if (scriptable instanceof DebuggableObject) {
			ids = ((DebuggableObject) scriptable).getAllIds();
		} else {
			ids = scriptable.getIds();
		}

		Scriptable proto = scriptable.getPrototype();
		Scriptable parent = scriptable.getParentScope();
		int extra = 0;
		if (proto != null) {
			++extra;
		}
		if (parent != null) {
			++extra;
		}
		if (extra != 0) {
			Object[] tmp = new Object[extra + ids.length];
			System.arraycopy(ids, 0, tmp, extra, ids.length);
			ids = tmp;
			extra = 0;
			if (proto != null) {
				ids[extra++] = "__proto__";
			}
			if (parent != null) {
				ids[extra++] = "__parent__";
			}
		}

		return ids;
	}

	/**
	 * Interrupts script execution.
	 * 
	 * @throws Exception
	 */
	private void interrupted(Context cx, final StackFrame frame, Throwable scriptException,
			boolean needException) throws Exception {
		ContextData contextData = frame.contextData();
		boolean eventThreadFlag = callback.isGuiEventThread();
		contextData.eventThreadFlag = eventThreadFlag;

		boolean recursiveEventThreadCall = false;

		interruptedCheck: synchronized (eventThreadMonitor) {
			if (eventThreadFlag) {
				if (interruptedContextData != null) {
					recursiveEventThreadCall = true;
					break interruptedCheck;
				}
			} else {
				while (interruptedContextData != null) {
					try {
						eventThreadMonitor.wait();
					} catch (InterruptedException exc) {
						return;
					}
				}
			}
			interruptedContextData = contextData;
		}

		if (recursiveEventThreadCall) {
			// XXX: For now the following is commented out as on Linux
			// too deep recursion of dispatchNextGuiEvent causes GUI lockout.
			// Note: it can make GUI unresponsive if long-running script
			// will be called on GUI thread while processing another interrupt
			if (false) {
				// Run event dispatch until gui sets a flag to exit the initial
				// call to interrupted.
				while (this.returnValue == -1) {
					try {
						callback.dispatchNextGuiEvent();
					} catch (InterruptedException exc) {
					}
				}
			}
			return;
		}

		if (interruptedContextData == null)
			Kit.codeBug();

		try {
			do {
				int frameCount = contextData.frameCount();
				this.frameIndex = frameCount - 1;

				final String threadTitle = Thread.currentThread().toString();
				final String alertMessage;
				if (scriptException == null) {
					alertMessage = null;
				} else {
					alertMessage = scriptException.toString();
				}

				int returnValue = -1;
				if (!eventThreadFlag) {
					synchronized (monitor) {
						// if (insideInterruptLoop)
						// Kit.codeBug();
						this.insideInterruptLoop = true;
						this.evalRequest = null;
						this.returnValue = -1;
						callback.enterInterrupt(frame, threadTitle, alertMessage);
						try {
							for (;;) {
								try {
									monitor.wait();
								} catch (InterruptedException exc) {
									Thread.currentThread().interrupt();
									break;
								}
								if (evalRequest != null) {
									this.evalResultString = null;
									try {
										Map<String, Object> resultMap = doEvalReturnStringAndObject(
																									cx,
																									evalFrame,
																									evalRequest,
																									0,
																									needException,
																									false);
										if (resultMap.size() == 0) {
											evalResultString = "";
											evalResult = null;
										} else {
											Set<String> key = resultMap.keySet();
											evalResultString = key.iterator().next();
											evalResult = resultMap.get(evalResultString);
										}
									} finally {
										evalRequest = null;
										evalFrame = null;
										monitor.notify();
									}
									continue;
								}
								if (this.returnValue != -1) {
									returnValue = this.returnValue;
									break;
								}
							}
						} finally {
							insideInterruptLoop = false;
						}
					}
				} else {
					this.returnValue = -1;
					callback.enterInterrupt(frame, threadTitle, alertMessage);
					while (this.returnValue == -1) {
						try {
							callback.dispatchNextGuiEvent();
						} catch (InterruptedException exc) {
						}
					}
					returnValue = this.returnValue;
				}
				callback.resumeInterrupt();
				switch (returnValue) {
				case EXIT:
					// while (contextData.frameCount() > 1) {
					// contextData.popFrame();
					// }
					// Context.getCurrentContext().
					// Context.exit();
					break;
				case STEP_OVER:
					contextData.breakNextLine = true;
					contextData.stopAtFrameDepth = contextData.frameCount();
					break;
				case STEP_INTO:
					contextData.breakNextLine = true;
					contextData.stopAtFrameDepth = -1;
					break;
				case STEP_OUT:
					if (contextData.frameCount() > 1) {
						contextData.breakNextLine = true;
						contextData.stopAtFrameDepth = contextData.frameCount() - 1;
					}
					break;
				}
			} while (false);
		} finally {
			synchronized (eventThreadMonitor) {
				interruptedContextData = null;
				eventThreadMonitor.notifyAll();
			}
		}

	}

	/**
	 * Evaluates script in the given stack frame.
	 * 
	 * @throws Exception
	 */
	public static Object doEvalReturnObject(Context cx, StackFrame frame, String expr, int lineIndex,
			boolean needException) throws Exception {
		Map<String, Object> resultMap = doEvalReturnStringAndObject(cx, frame, expr, lineIndex,
																	needException, false);

		if (resultMap.isEmpty())
			return null;

		return resultMap.get(resultMap.keySet().iterator().next());
	}

	/**
	 * Evaluates script in the given stack frame.
	 * 
	 * @throws Exception
	 */
	private static Map<String, Object> doEvalReturnStringAndObject(Context cx, StackFrame frame, String expr,
			int lineIndex, boolean needException, boolean needPrintScriptLog) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String str;
		Object obj = null;
		Debugger saved_debugger = cx.getDebugger();
		Object saved_data = cx.getDebuggerContextData();
		int saved_level = cx.getOptimizationLevel();

		cx.setDebugger(null, null);
		cx.setOptimizationLevel(-1);
		cx.setGeneratingDebug(false);
		Script script = null;
		try {
			script = (Script) cx.compileString(expr, "", lineIndex, null);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw e;
		}
		long startTime = 0;
		try {
			Scriptable scope = null;
			Scriptable thisObj = null;
			if (frame != null) {
				scope = frame.scope;
				thisObj = frame.thisObj;
			} else {
				scope = Global.getInstance();
				thisObj = Global.getInstance();
			}
			// obj = script.call(cx, scope, thisObj, ScriptRuntime.emptyArgs);

			RhinoStandardFunction.resetTotalSleepTime();
			startTime = System.currentTimeMillis();
			if (needPrintScriptLog) {
				System.out.println("Starting execute");
			}
			obj = script.exec(cx, scope);
			if (obj == Undefined.instance) {
				str = "";
			} else {
				str = ScriptRuntime.toString(obj);
			}
		} catch (Exception exc) {
			if (logger.isDebugEnabled()) {
				logger.debug(exc.getMessage(), exc);
			}
			if (needException) {
				// logger.error(exc.getMessage(), exc);
				throw exc;
			}
			str = exc.getMessage();
		} finally {
			long realExecuteTime = System.currentTimeMillis() - startTime;
			if (needPrintScriptLog) {
				System.out.println(new Timestamp(System.currentTimeMillis())
									+ ": Execute Finished. Total execute time: "
									+ realExecuteTime
									+ "ms. Total sleep time: "
									+ RhinoStandardFunction.getTotalSleepTime()
									+ "ms. Real execute time: "
									+ (realExecuteTime - RhinoStandardFunction.getTotalSleepTime())
									+ "ms.");
			}
			RhinoStandardFunction.resetTotalSleepTime();
			cx.setGeneratingDebug(true);
			cx.setOptimizationLevel(saved_level);
			cx.setDebugger(saved_debugger, saved_data);
		}
		if (str == null) {
			str = "null";
		}
		if (obj != null)
			result.put(str, obj);
		else if (!needException)
			result.put(str, str);
		return result;
	}

	/**
	 * Evaluates script in the given stack frame.
	 * 
	 * @throws Exception
	 */
	private static String doEval(Context cx, StackFrame frame, String expr, int lineIndex,
			boolean needException) throws Exception {
		Map<String, Object> resultMap = doEvalReturnStringAndObject(cx, frame, expr, lineIndex,
																	needException, false);

		if (resultMap.isEmpty())
			return "";

		return resultMap.keySet().iterator().next();
	}

	// /**
	// * Evaluates script in the given stack frame.
	// */
	// private static String do_eval(Context cx, StackFrame frame, String expr)
	// {
	// String resultString;
	// Debugger saved_debugger = cx.getDebugger();
	// Object saved_data = cx.getDebuggerContextData();
	// int saved_level = cx.getOptimizationLevel();
	//
	// cx.setDebugger(null, null);
	// cx.setOptimizationLevel(-1);
	// cx.setGeneratingDebug(false);
	// try {
	// Callable script = (Callable)cx.compileString(expr, "", 0, null);
	// Object result = script.call(cx, frame.scope, frame.thisObj,
	// ScriptRuntime.emptyArgs);
	// if (result == Undefined.instance) {
	// resultString = "";
	// } else {
	// resultString = ScriptRuntime.toString(result);
	// }
	// } catch (Exception exc) {
	// resultString = exc.getMessage();
	// } finally {
	// cx.setGeneratingDebug(true);
	// cx.setOptimizationLevel(saved_level);
	// cx.setDebugger(saved_debugger, saved_data);
	// }
	// if (resultString == null) {
	// resultString = "null";
	// }
	// return resultString;
	// }

	/**
	 * Proxy class to implement debug interfaces without bloat of class files.
	 */
	private static class DimIProxy implements ContextAction, ContextFactory.Listener, Debugger {

		/**
		 * The debugger.
		 */
		private RhinoDim dim;

		/**
		 * The interface implementation type. One of the IPROXY_* constants
		 * defined in {@link RhinoDim}.
		 */
		private int type;

		/**
		 * The URL origin of the script to compile or evaluate.
		 */
		private String url;

		/**
		 * The text of the script to compile, evaluate or test for compilation.
		 */
		private String text;

		/**
		 * The object to convert, get a property from or enumerate.
		 */
		private Object object;

		/**
		 * The property to look up in {@link #object}.
		 */
		private Object id;

		/**
		 * The boolean result of the action.
		 */
		private boolean booleanResult;

		/**
		 * The String result of the action.
		 */
		private String stringResult;

		/**
		 * The Object result of the action.
		 */
		private Object objectResult;

		/**
		 * The Object[] result of the action.
		 */
		private Object[] objectArrayResult;

		/**
		 * Creates a new DimIProxy.
		 */
		private DimIProxy(RhinoDim dim, int type) {
			this.dim = dim;
			this.type = type;
		}

		// ContextAction

		/**
		 * Performs the action given by {@link #type}.
		 */
		public Object run(Context cx) {
			switch (type) {
			case IPROXY_COMPILE_SCRIPT:
				try {
					cx.compileString(text, url, 1, null);
				} catch (Exception e) {
				}
				break;

			case IPROXY_EVAL_SCRIPT: {
				Scriptable scope = null;
				if (dim.scopeProvider != null) {
					scope = dim.scopeProvider.getScope();
				}
				if (scope == null) {
					scope = new ImporterTopLevel(cx);
				}
				long startTime = System.currentTimeMillis();

				RhinoStandardFunction.resetTotalSleepTime();
				System.out.println("Starting execute");
				RuntimeException exception = null;
				try {
					cx.evaluateString(scope, text, url, 1, null);
				} catch (RuntimeException e) {
					exception = e;
					logger.error(e);
				} finally {
				}
				long realExecuteTime = System.currentTimeMillis() - startTime;
				System.out.println(new Timestamp(System.currentTimeMillis())
									+ ": Execute Finished. Total execute time: "
									+ realExecuteTime
									+ "ms. Total sleep time: "
									+ RhinoStandardFunction.getTotalSleepTime()
									+ "ms. Real execute time: "
									+ (realExecuteTime - RhinoStandardFunction.getTotalSleepTime())
									+ "ms.");

				RhinoStandardFunction.resetTotalSleepTime();
				if (exception != null)
					throw exception;
			}
				break;

			case IPROXY_STRING_IS_COMPILABLE:
				booleanResult = cx.stringIsCompilableUnit(text);
				break;

			case IPROXY_OBJECT_TO_STRING:
				// if (object == Undefined.instance) {
				// stringResult = "undefined";
				// } else if (object == null) {
				// stringResult = "null";
				// } else if (object instanceof NativeCall) {
				// stringResult = "[object Call]";
				// } else {
				// stringResult = Context.toString(object);
				// }
				stringResult = ObjectUtils.toString(object);
				break;

			case IPROXY_OBJECT_PROPERTY:
				objectResult = dim.getObjectPropertyImpl(cx, object, id);
				break;

			case IPROXY_OBJECT_IDS:
				objectArrayResult = dim.getObjectIdsImpl(cx, object);
				break;

			default:
				throw Kit.codeBug();
			}
			return null;
		}

		/**
		 * Performs the action given by {@link #type} with the attached
		 * {@link ContextFactory}.
		 */
		private void withContext() {
			try {
				dim.contextFactory.call(this);
			} catch (RuntimeException e) {
			    logger.error(e);
				if (e.getMessage() != null && e.getMessage().length() > 0)
					throw e;
			}
		}

		// ContextFactory.Listener

		/**
		 * Called when a Context is created.
		 */
		public void contextCreated(Context cx) {
			if (type != IPROXY_LISTEN)
				Kit.codeBug();
			ContextData contextData = new ContextData();
			Debugger debugger = new DimIProxy(dim, IPROXY_DEBUG);
			cx.setDebugger(debugger, contextData);
			cx.setGeneratingDebug(true);
			cx.setOptimizationLevel(-1);
		}

		/**
		 * Called when a Context is destroyed.
		 */
		public void contextReleased(Context cx) {
			if (type != IPROXY_LISTEN)
				Kit.codeBug();
		}

		// Debugger

		/**
		 * Returns a StackFrame for the given function or script.
		 */
		public DebugFrame getFrame(Context cx, DebuggableScript fnOrScript) {
			if (type != IPROXY_DEBUG)
				Kit.codeBug();

			FunctionSource item = dim.getFunctionSource(fnOrScript);
			if (item == null) {
				// Can not debug if source is not available
				return null;
			}
			return new StackFrame(cx, dim, item);
		}

		/**
		 * Called when compilation is finished.
		 */
		public void handleCompilationDone(Context cx, DebuggableScript fnOrScript, String source) {
			if (type != IPROXY_DEBUG)
				Kit.codeBug();

			if (!fnOrScript.isTopLevel()) {
				return;
			}
			dim.registerTopScript(fnOrScript, source);
		}
	}

	/**
	 * Class to store information about a stack.
	 */
	public static class ContextData {

		/**
		 * The stack frames.
		 */
		private ObjArray frameStack = new ObjArray();

		/**
		 * Whether the debugger should break at the next line in this context.
		 */
		private boolean breakNextLine;

		/**
		 * The frame depth the debugger should stop at. Used to implement
		 * "step over" and "step out".
		 */
		private int stopAtFrameDepth = -1;

		/**
		 * Whether this context is in the event thread.
		 */
		private boolean eventThreadFlag;

		/**
		 * The last exception that was processed.
		 */
		private Throwable lastProcessedException;

		/**
		 * Returns the ContextData for the given Context.
		 */
		public static ContextData get(Context cx) {
			return (ContextData) cx.getDebuggerContextData();
		}

		/**
		 * Returns the number of stack frames.
		 */
		public int frameCount() {
			return frameStack.size();
		}

		/**
		 * Returns the stack frame with the given index.
		 */
		public StackFrame getFrame(int frameNumber) {
			int num = frameStack.size() - frameNumber - 1;
			return (StackFrame) frameStack.get(num);
		}

		/**
		 * Pushes a stack frame on to the stack.
		 */
		private void pushFrame(StackFrame frame) {
			frameStack.push(frame);
		}

		/**
		 * Pops a stack frame from the stack.
		 */
		private void popFrame() {
			frameStack.pop();
		}
	}

	/**
	 * Object to represent one stack frame.
	 */
	public static class StackFrame implements DebugFrame {

		/**
		 * The debugger.
		 */
		private RhinoDim dim;

		/**
		 * The ContextData for the Context being debugged.
		 */
		private ContextData contextData;

		/**
		 * The scope.
		 */
		private Scriptable scope;

		/**
		 * The 'this' object.
		 */
		private Scriptable thisObj;

		/**
		 * Information about the function.
		 */
		private FunctionSource fsource;

		/**
		 * Array of breakpoint state for each source line.
		 */
		private boolean[] breakpoints;

		/**
		 * Current line number.
		 */
		private int lineNumber;

		/**
		 * Creates a new StackFrame.
		 */
		private StackFrame(Context cx, RhinoDim dim, FunctionSource fsource) {
			this.dim = dim;
			this.contextData = ContextData.get(cx);
			this.fsource = fsource;
			this.breakpoints = fsource.sourceInfo().breakpoints;
			this.lineNumber = fsource.firstLine();
		}

		/**
		 * Called when the stack frame is entered.
		 */
		public void onEnter(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			contextData.pushFrame(this);
			this.scope = scope;
			this.thisObj = thisObj;
			if (dim.breakOnEnter) {
				dim.handleBreakpointHit(this, cx);
			}
		}

		/**
		 * Called when the current position has changed.
		 */
		public void onLineChange(Context cx, int lineno) {
			this.lineNumber = lineno;

			if (!breakpoints[lineno] && !dim.breakFlag) {
				boolean lineBreak = contextData.breakNextLine;
				if (lineBreak && contextData.stopAtFrameDepth >= 0) {
					lineBreak = (contextData.frameCount() <= contextData.stopAtFrameDepth);
				}
				if (!lineBreak) {
					return;
				}
				contextData.stopAtFrameDepth = -1;
				contextData.breakNextLine = false;
			}

			dim.handleBreakpointHit(this, cx);
		}

		/**
		 * Called when an exception has been thrown.
		 */
		public void onExceptionThrown(Context cx, Throwable exception) {
			dim.handleExceptionThrown(cx, exception, this);
		}

		/**
		 * Called when the stack frame has been left.
		 */
		public void onExit(Context cx, boolean byThrow, Object resultOrException) {
			if (dim.breakOnReturn && !byThrow) {
				dim.handleBreakpointHit(this, cx);
			}
			contextData.popFrame();
		}

		/**
		 * Called when a 'debugger' statement is executed.
		 */
		public void onDebuggerStatement(Context cx) {
			dim.handleBreakpointHit(this, cx);
		}

		/**
		 * Returns the SourceInfo object for the function.
		 */
		public SourceInfo sourceInfo() {
			return fsource.sourceInfo();
		}

		/**
		 * Returns the ContextData object for the Context.
		 */
		public ContextData contextData() {
			return contextData;
		}

		/**
		 * Returns the scope object for this frame.
		 */
		public Object scope() {
			return scope;
		}

		/**
		 * Returns the 'this' object for this frame.
		 */
		public Object thisObj() {
			return thisObj;
		}

		/**
		 * Returns the source URL.
		 */
		public String getUrl() {
			return fsource.sourceInfo().url();
		}

		/**
		 * Returns the current line number.
		 */
		public int getLineNumber() {
			return lineNumber;
		}
	}

	/**
	 * Class to store information about a function.
	 */
	public static class FunctionSource {

		/**
		 * Information about the source of the function.
		 */
		private SourceInfo sourceInfo;

		/**
		 * Line number of the first line of the function.
		 */
		private int firstLine;

		/**
		 * The function name.
		 */
		private String name;

		/**
		 * Creates a new FunctionSource.
		 */
		private FunctionSource(SourceInfo sourceInfo, int firstLine, String name) {
			if (name == null)
				throw new IllegalArgumentException();
			this.sourceInfo = sourceInfo;
			this.firstLine = firstLine;
			this.name = sourceInfo.url() + "." + name;

		}

		/**
		 * Returns the SourceInfo object that describes the source of the
		 * function.
		 */
		public SourceInfo sourceInfo() {
			return sourceInfo;
		}

		/**
		 * Returns the line number of the first line of the function.
		 */
		public int firstLine() {
			return firstLine;
		}

		/**
		 * Returns the name of the function.
		 */
		public String name() {
			return name;
		}
	}

	/**
	 * Class to store information about a script source.
	 */
	public static class SourceInfo {

		/**
		 * An empty array of booleans.
		 */
		private static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];

		/**
		 * The script.
		 */
		private String source;

		/**
		 * The URL of the script.
		 */
		private String url;
		/**
		 * The time of the script has been update.
		 */
		private long lastUpdateTime;

		/**
		 * @param lastUpdateTime
		 *            the lastUpdateTime to set
		 */
		public void setLastUpdateTime(long lastUpdateTime) {
			this.lastUpdateTime = lastUpdateTime;
		}

		/**
		 * @return the lastUpdateTime
		 */
		public long getLastUpdateTime() {
			return lastUpdateTime;
		}

		/**
		 * Array indicating which lines can have breakpoints set.
		 */
		private boolean[] breakableLines;

		/**
		 * Array indicating whether a breakpoint is set on the line.
		 */
		private boolean[] breakpoints;

		/**
		 * Array of FunctionSource objects for the functions in the script.
		 */
		private FunctionSource[] functionSources;

		/**
		 * Creates a new SourceInfo object.
		 */
		public static SourceInfo newInstance(String source, DebuggableScript[] functions, String normilizedUrl) {
			return new SourceInfo(source, functions, normilizedUrl);
		}

		/**
		 * Creates a new SourceInfo object.
		 */
		private SourceInfo(String source, DebuggableScript[] functions, String normilizedUrl) {
			this.source = source;
			this.url = normilizedUrl;
			File f = new File(normilizedUrl);
			lastUpdateTime = f.lastModified();

			if (functions != null) {
				int N = functions.length;
				int[][] lineArrays = new int[N][];
				for (int i = 0; i != N; ++i) {
					lineArrays[i] = functions[i].getLineNumbers();
				}

				int minAll = 0, maxAll = -1;
				int[] firstLines = new int[N];
				for (int i = 0; i != N; ++i) {
					int[] lines = lineArrays[i];
					if (lines == null || lines.length == 0) {
						firstLines[i] = -1;
					} else {
						int min, max;
						min = max = lines[0];
						for (int j = 1; j != lines.length; ++j) {
							int line = lines[j];
							if (line < min) {
								min = line;
							} else if (line > max) {
								max = line;
							}
						}
						firstLines[i] = min;
						if (minAll > maxAll) {
							minAll = min;
							maxAll = max;
						} else {
							if (min < minAll) {
								minAll = min;
							}
							if (max > maxAll) {
								maxAll = max;
							}
						}
					}
				}

				if (minAll > maxAll) {
					// No line information
					this.breakableLines = EMPTY_BOOLEAN_ARRAY;
					this.breakpoints = EMPTY_BOOLEAN_ARRAY;
				} else {
					if (minAll < 0) {
						// Line numbers can not be negative
						throw new IllegalStateException(String.valueOf(minAll));
					}
					int linesTop = maxAll + 1;
					this.breakableLines = new boolean[linesTop];
					this.breakpoints = new boolean[linesTop];
					for (int i = 0; i != N; ++i) {
						int[] lines = lineArrays[i];
						if (lines != null && lines.length != 0) {
							for (int j = 0; j != lines.length; ++j) {
								int line = lines[j];
								this.breakableLines[line] = true;
							}
						}
					}
				}
				this.functionSources = new FunctionSource[N];
				int anonymousFunctionIndex = 1;
				for (int i = 0; i != N; ++i) {
					String name = functions[i].getFunctionName();
					// if (name == null || name.length() == 0) {
					// name = "anonymousFunction_" + anonymousFunctionIndex;
					// ++anonymousFunctionIndex;
					// }
					if (name == null) {
						name = "";
					}
					this.functionSources[i] = new FunctionSource(this, firstLines[i], name);
				}
			}
		}

		/**
		 * Returns the source text.
		 */
		public String source() {
			return this.source;
		}

		/**
		 * Returns the script's origin URL.
		 */
		public String url() {
			return this.url;
		}

		/**
		 * Returns the number of FunctionSource objects stored in this object.
		 */
		public int functionSourcesTop() {
			return functionSources.length;
		}

		/**
		 * Returns the FunctionSource object with the given index.
		 */
		public FunctionSource functionSource(int i) {
			return functionSources[i];
		}

		/**
		 * Copies the breakpoints from the given SourceInfo object into this
		 * one.
		 */
		private void copyBreakpointsFrom(SourceInfo old) {
			int end = old.breakpoints.length;
			if (end > this.breakpoints.length) {
				end = this.breakpoints.length;
			}
			for (int line = 0; line != end; ++line) {
				if (old.breakpoints[line]) {
					this.breakpoints[line] = true;
				}
			}
		}

		/**
		 * Returns whether the given line number can have a breakpoint set on
		 * it.
		 */
		public boolean breakableLine(int line) {
			if (this.breakableLines == null)
				return false;
			return (line < this.breakableLines.length) && this.breakableLines[line];
		}

		/**
		 * Returns whether there is a breakpoint set on the given line.
		 */
		public boolean breakpoint(int line) {
			if (!breakableLine(line)) {
				throw new IllegalArgumentException(String.valueOf(line));
			}
			return line < this.breakpoints.length && this.breakpoints[line];
		}

		/**
		 * Sets or clears the breakpoint flag for the given line.
		 */
		public boolean breakpoint(int line, boolean value) {
			if (!breakableLine(line)) {
				throw new IllegalArgumentException(String.valueOf(line));
			}
			boolean changed;
			synchronized (breakpoints) {
				if (breakpoints[line] != value) {
					breakpoints[line] = value;
					changed = true;
				} else {
					changed = false;
				}
			}
			return changed;
		}

		/**
		 * Removes all breakpoints from the script.
		 */
		public void removeAllBreakpoints() {
			if (breakpoints != null) {
				synchronized (breakpoints) {
					for (int line = 0; line != breakpoints.length; ++line) {
						breakpoints[line] = false;
					}
				}
			}
		}
	}

	@Override
	public void execute(String scriptPath) throws Exception {
		try {
			if (scriptPath.indexOf("/..") >= 0 || scriptPath.indexOf(File.separator+".." ) >= 0) {
				File f = new File(scriptPath);
				Stack<String> stack = new Stack<String>();
				int parentCount = 0;
				String filePath =f.getAbsolutePath(); 
				while (filePath.indexOf("/..") >= 0
						|| filePath.indexOf(File.separator + "..") >= 0) {

					String filename = filePath.substring(Math.max(filePath.lastIndexOf("/"),filePath.lastIndexOf(File.separator))+1);
					filePath = filePath.substring(0,filePath.length() - filename.length() -1);				
					if (filename.equals("..")) {
						++parentCount;						
					} else {
						stack.push(filename);
					}
				}
				while(parentCount>0){
					String filename = filePath.substring(Math.max(filePath.lastIndexOf("/"),filePath.lastIndexOf(File.separator))+1);
					filePath = filePath.substring(0,filePath.length() - filename.length() -1);					
					--parentCount;
				}
				scriptPath = filePath;
				while (!stack.isEmpty()) {
					scriptPath = scriptPath + File.separator + stack.pop();
				}
			}
			
			String script = FileUtils.readFile(scriptPath, "UTF-8");
			evalScript(scriptPath, script);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
}

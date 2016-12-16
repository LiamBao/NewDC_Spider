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

package com.cic.datacrawl.ui;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

import javax.swing.JFrame;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Scriptable;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.AutoSaveConfiguration;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.initialize.InitializerRegister;
import com.cic.datacrawl.core.initialize.RunOnStartup;
import com.cic.datacrawl.core.rhino.debugger.RhinoDim;
import com.cic.datacrawl.core.rhino.debugger.ScopeProvider;
import com.cic.datacrawl.core.rhino.shell.Global;
import com.cic.datacrawl.management.entity.SubTask;
import com.cic.datacrawl.management.entity.Task;
import com.cic.datacrawl.management.manager.SubTaskManager;
import com.cic.datacrawl.ui.dialog.ChooseWorkspaceDialog;
import com.cic.datacrawl.ui.utils.WindowUtils;

/**
 * Rhino script debugger main class. This class links together a debugger object
 * ({@link RhinoDim}) and a debugger GUI object ({@link SwingGui}).
 */
public class Main {
	protected static final Logger LOG = Logger.getLogger(Main.class);
	/**
	 * The debugger.7
	 */
	private RhinoDim dim;

	/**
	 * The debugger frame.
	 */
	private SwingGui debugGui;

	/**
	 * Creates a new Main.
	 */
	public Main(String title) {
		dim = new RhinoDim();
		debugGui = new SwingGui(dim, title);
	}

	/**
	 * Returns the debugger window {@link JFrame}.
	 */
	public JFrame getDebugFrame() {
		return debugGui;
	}

	/**
	 * Breaks execution of the script.
	 */
	public void doBreak() {
		dim.setBreak();
	}

	/**
	 * Sets whether execution should break when a script exception is thrown.
	 */
	public void setBreakOnExceptions(boolean value) {
		dim.setBreakOnExceptions(value);
		debugGui.getMenubar().getBreakOnExceptions().setSelected(value);
	}

	/**
	 * Sets whether execution should break when a function is entered.
	 */
	public void setBreakOnEnter(boolean value) {
		dim.setBreakOnEnter(value);
		debugGui.getMenubar().getBreakOnEnter().setSelected(value);
	}

	/**
	 * Sets whether execution should break when a function is left.
	 */
	public void setBreakOnReturn(boolean value) {
		dim.setBreakOnReturn(value);
		debugGui.getMenubar().getBreakOnReturn().setSelected(value);
	}

	/**
	 * Removes all breakpoints.
	 */
	public void clearAllBreakpoints() {
		dim.clearAllBreakpoints();
	}

	/**
	 * Resumes execution of the script.
	 */
	public void go() {
		dim.go();
	}

	/**
	 * Sets the scope to be used for script evaluation.
	 */
	public void setScope(Scriptable scope) {
		setScopeProvider(IProxy.newScopeProvider(scope));
	}

	private void setGlobal(Global global) {
		debugGui.setGlobal(global);
	}

	/**
	 * Sets the {@link ScopeProvider} that provides a scope to be used for
	 * script evaluation.
	 */
	public void setScopeProvider(ScopeProvider p) {
		dim.setScopeProvider(p);
	}

	/**
	 * Assign a Runnable object that will be invoked when the user selects
	 * "Exit..." or closes the Debugger main window.
	 */
	public void setExitAction(Runnable r) {
		debugGui.setExitAction(r);
	}

	/**
	 * Returns an {@link InputStream} for stdin from the debugger's internal
	 * Console window.
	 */
	public InputStream getIn() {
		return debugGui.getConsole().getIn();
	}

	/**
	 * Returns a {@link PrintStream} for stdout to the debugger's internal
	 * Console window.
	 */
	public PrintStream getOut() {
		return debugGui.getOutputConsole().getOut();
	}

	/**
	 * Returns a {@link PrintStream} for stderr in the Debugger's internal
	 * Console window.
	 */
	public PrintStream getErr() {
		return debugGui.getOutputConsole().getErr();
	}

	/**
	 * Packs the debugger GUI frame.
	 */
	public void pack() {
		debugGui.pack();
	}

	public void startupBrowser() {
		debugGui.startupBrowser();
	}

	/**
	 * Sets the debugger GUI frame dimensions.
	 */
	public void setSize(int w, int h) {
		debugGui.setSize(w, h);
	}

	/**
	 * Sets the visibility of the debugger GUI frame.
	 */
	public void setVisible(boolean flag) {
		WindowUtils.showFrameAtScreenCenter(debugGui);
		debugGui.setVisible(flag);
	}

	/**
	 * Returns whether the debugger GUI frame is visible.
	 */
	public boolean isVisible() {
		return debugGui.isVisible();
	}

	/**
	 * Frees any resources held by the debugger.
	 */
	public void dispose() {
		clearAllBreakpoints();
		dim.go();
		debugGui.dispose();
		dim = null;
	}

	/**
	 * Attaches the debugger to the given {@link ContextFactory}.
	 */
	public void attachTo(ContextFactory factory) {
		dim.attachTo(factory);
	}

	/**
	 * Detaches from the current {@link ContextFactory}.
	 */
	public void detach() {
		dim.detach();
	}

	private static void startupGUI(String[] args) {
		// 标识当前运行环境是编辑器
		boolean isShowChooseWorkspace = true;

		Main main = new Main(SwingGui.FRAME_TITLE);

		if (isShowChooseWorkspace) {
			ChooseWorkspaceDialog dialog = new ChooseWorkspaceDialog(main.debugGui);
			dialog.setStartupArgs(args);
			dialog.setVisible(true);
		 }
		Global global = Global.getInstance();

		main.setExitAction(new IProxy(IProxy.EXIT_ACTION));

		main.attachTo(com.cic.datacrawl.ui.shell.Shell.shellContextFactory);

		main.setScope(global);
		main.setGlobal(global);
		main.pack();

		main.startupBrowser();
		main.maxSize();
		if (!isShowChooseWorkspace) {
			main.setVisible(true);
		 }

		if (args.length == 2) {
			args = new String[0];
		}

		global.setIn(main.getIn());
		global.setOut(main.getOut());
		global.setErr(main.getErr());

		System.setIn(main.getIn());
		System.setOut(main.getOut());
		System.setErr(main.getErr());

		com.cic.datacrawl.ui.shell.Shell.setDebugGui(main.debugGui);

		final RhinoDim finalDim = main.dim;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (InitializerRegister.getInstance().isNotFinished())
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				((RunOnStartup) ApplicationContext.getInstance().getBean("runOnStartup")).execute(finalDim);
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
					}
					System.gc();
				}
			}
		}).start();
		// 自动保存设置
		AutoSaveConfiguration autoSaveRunner = new AutoSaveConfiguration();
		autoSaveRunner.registerSaveRunner(main.debugGui);
		autoSaveRunner.setSleepSecond(3600);
		new Thread(autoSaveRunner).start();

		com.cic.datacrawl.ui.shell.Shell.exec(args, global);
	}

	/**
	 * Main entry point. Creates a debugger attached to a Rhino
	 * {@link com.cic.datacrawl.ui.shell.Shell} shell session.
	 */
	public static void main(final String[] args) {
		if (System.getProperties().get("os.name").toString().toLowerCase().indexOf("linux") >= 0)
			System.setProperty("sun.awt.xembedserver", "true");
		try {
			String path = null;
			boolean reflash = true;
			if (args != null && args.length > 0) {
				if (args[0].equalsIgnoreCase("-h")) {
					System.out.println("Command Format: \n"
										+ "\t%JAVA_HOME%\\BIN\\JAVA -jar homepageCatcher.jar "
										+ "[-d config path]\n"
										+ "\t%JAVA_HOME%\\BIN\\JAVA -jar homepageCatcher.jar "
										+ "[-d config path_1;path_2;....;path_n]\n");
				}

				int pathIndex = ArrayUtils.indexOf(args, "-d");
				if (pathIndex >= 0) {
					try {
						path = args[pathIndex + 1];
						File f = new File(path);
						if (!f.exists()) {
							LOG.warn("Invalid path of configuration. " + "Using default configuration.");
						}
					} catch (Throwable e) {
						LOG.warn("Invalid path of configuration. " + "Using default configuration.");
					}
				}
				// int reflashIndex = ArrayUtils.indexOf(args, "-r");
				//
				// reflash = new Boolean(args[reflashIndex + 1]).booleanValue();
			}
			if (path == null || path.trim().length() == 0)
				path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";

			LOG.debug("Config Path: \"" + path + "\"");
			// 启动IOC容器
			// 启动配置管理程序
			// 装载默认配置文件
			ApplicationContext.initialiaze(path, reflash);
			System.setProperty(ApplicationContext.CONFIG_PATH, path);
			// 注册js环境中需要用到的全局变量和全局方法
			InitializerRegister.getInstance().execute();
			// 初始化验证码输入框
			//VerifyCodeInputDialog.init();
			// 初始化测试用数据
			//initTestData();
			// 启动GUI
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					startupGUI(args);
				}
			});
			thread.setName("UI_Thread");
			thread.start();

		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static void initTestData() {
		long taskRecordId = 0;
		if (ApplicationContext.getInstance().containsBean("subTaskDefineDemoData")) {

			Task task = (Task) ApplicationContext.getInstance().getBean("taskDefineDemoData");
			if (task == null) {
				LOG.warn("No demo data need initilaze.");
				return;
			}
			if (ApplicationContext.getInstance().containsBean("subTaskDefineDemoData")) {
				SubTaskManager subTaskManager = (SubTaskManager) ApplicationContext.getInstance()
						.getBean("taskExecuteRecordManager");

				// 创建任务开始记录
//				SubTask taskRecord = subTaskManager.getNextTask();
//				if (taskRecord != null)
//					taskRecordId = taskRecord.getId();
			}
		}
		System.setProperty("Task.Record.Id", "" + taskRecordId);
	}

	private void changeLayout() {
		debugGui.changeLayout();
	}

	/**
	 * Sets the debugger GUI frame dimensions.
	 */
	public void maxSize() {
		debugGui.setExtendedState(JFrame.MAXIMIZED_BOTH);
		changeLayout();
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Dimension screenSize = Toolkit.getDefaultToolkit()
		// .getScreenSize();
		// while (debugGui.getX() > 0 || debugGui.getY() > 0
		// || debugGui.getWidth() < screenSize.width) {
		// try {
		// Thread.sleep(30);
		// } catch (InterruptedException e) {
		// }
		// }
		// changeLayout();
		// }
		// }).start();
	}

	/**
	 * Entry point for embedded applications. This method attaches to the global
	 * {@link ContextFactory} with a scope of a newly created {@link Global}
	 * object. No I/O redirection is performed as with {@link #main(String[])}.
	 */
	public static void mainEmbedded(String title) {
		ContextFactory factory = ContextFactory.getGlobal();
		Global global = Global.getInstance();
		global.init(factory);
		mainEmbedded(factory, global, title);
	}

	/**
	 * Entry point for embedded applications. This method attaches to the given
	 * {@link ContextFactory} with the given scope. No I/O redirection is
	 * performed as with {@link #main(String[])}.
	 */
	public static void mainEmbedded(ContextFactory factory, Scriptable scope, String title) {
		mainEmbeddedImpl(factory, scope, title);
	}

	/**
	 * Entry point for embedded applications. This method attaches to the given
	 * {@link ContextFactory} with the given scope. No I/O redirection is
	 * performed as with {@link #main(String[])}.
	 */
	public static void mainEmbedded(ContextFactory factory, ScopeProvider scopeProvider, String title) {
		mainEmbeddedImpl(factory, scopeProvider, title);
	}

	/**
	 * Helper method for {@link #mainEmbedded(String)}, etc.
	 */
	private static void mainEmbeddedImpl(ContextFactory factory, Object scopeProvider, String title) {
		if (title == null) {
			title = "Rhino JavaScript Debugger (embedded usage)";
		}
		Main main = new Main(title);
		// main.doBreak();
		main.setExitAction(new IProxy(IProxy.EXIT_ACTION));

		main.attachTo(factory);
		if (scopeProvider instanceof ScopeProvider) {
			main.setScopeProvider((ScopeProvider) scopeProvider);
		} else {
			Scriptable scope = (Scriptable) scopeProvider;
			if (scope instanceof Global) {
				Global global = (Global) scope;
				global.setIn(main.getIn());
				global.setOut(main.getOut());
				global.setErr(main.getErr());
			}
			main.setScope(scope);
		}

		main.pack();
		main.setSize(600, 460);
		main.setVisible(true);
	}

	// Deprecated methods

	/**
	 * @deprecated Use {@link #setSize(int, int)} instead.
	 */
	public void setSize(java.awt.Dimension dimension) {
		debugGui.setSize(dimension.width, dimension.height);
	}

	/**
	 * @deprecated The method does nothing and is only present for
	 *             compatibility.
	 */
	public void setOptimizationLevel(int level) {
	}

	/**
	 * @deprecated The method is only present for compatibility and should not
	 *             be called.
	 */
	public void contextEntered(Context cx) {
		throw new IllegalStateException();
	}

	/**
	 * @deprecated The method is only present for compatibility and should not
	 *             be called.
	 */
	public void contextExited(Context cx) {
		throw new IllegalStateException();
	}

	/**
	 * @deprecated The method is only present for compatibility and should not
	 *             be called.
	 */
	public void contextCreated(Context cx) {
		throw new IllegalStateException();
	}

	/**
	 * @deprecated The method is only present for compatibility and should not
	 *             be called.
	 */
	public void contextReleased(Context cx) {
		throw new IllegalStateException();
	}

	/**
	 * Class to consolidate all internal implementations of interfaces to avoid
	 * class generation bloat.
	 */
	private static class IProxy implements Runnable, ScopeProvider {

		// Constants for 'type'.
		public static final int EXIT_ACTION = 1;
		public static final int SCOPE_PROVIDER = 2;

		/**
		 * The type of interface.
		 */
		private final int type;

		/**
		 * The scope object to expose when {@link #type} =
		 * {@link #SCOPE_PROVIDER}.
		 */
		private Scriptable scope;

		/**
		 * Creates a new IProxy.
		 */
		public IProxy(int type) {
			this.type = type;
		}

		/**
		 * Creates a new IProxy that acts as a {@link ScopeProvider}.
		 */
		public static ScopeProvider newScopeProvider(Scriptable scope) {
			IProxy scopeProvider = new IProxy(SCOPE_PROVIDER);
			scopeProvider.scope = scope;
			return scopeProvider;
		}

		// ContextAction

		/**
		 * Exit action.
		 */
		public void run() {
			if (type != EXIT_ACTION)
				Kit.codeBug();
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
			}
			System.exit(0);
		}

		// ScopeProvider

		/**
		 * Returns the scope for script evaluations.
		 */
		public Scriptable getScope() {
			if (type != SCOPE_PROVIDER)
				Kit.codeBug();
			if (scope == null)
				Kit.codeBug();
			return scope;
		}
	}
}

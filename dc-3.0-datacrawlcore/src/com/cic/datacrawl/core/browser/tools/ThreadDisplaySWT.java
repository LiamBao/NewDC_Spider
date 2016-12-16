package com.cic.datacrawl.core.browser.tools;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** SWT组件的消息分发线程(用于Swing或后台程序中) */
public class ThreadDisplaySWT extends Thread {
	private static final Logger LOG = Logger.getLogger(ThreadDisplaySWT.class);

	private ThreadDisplaySWT() {

	}

	private Display display;
	private Shell shell;

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	private synchronized boolean isShellNotDisposed() {
		if (shell == null)
			return true;
		return !shell.isDisposed();
	}

	@Override
	public void run() {
		display = new Display();
		// if (Config.isLinux()) {
		Thread wakeupThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(12000);
					} catch (InterruptedException e) {
					}
					LOG.trace("Do Display.wakeup");
					display.wake();
				}
			}
		});
		wakeupThread.setName("WakeupThread");
		wakeupThread.start();
		// }

		while (isShellNotDisposed()) {
			// if (clearingMessages) {
			// if (! OS.PeekMessage (display.msg, 0, 0, 0, OS.PM_REMOVE))
			// display.sleep();
			//
			// }
			// else {
			//if (shell != null) {
			
				try {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				} catch (Throwable e) {
					LOG.error(e.getMessage(),e);					
				}
			//}
			// }
		}
		
		if(!isShellNotDisposed()){
			LOG.info("display is disposed");
			System.out.println("display is disposed");
			System.exit(0);
		}
	}

	private volatile static ThreadDisplaySWT displayThread;

	private boolean isStarted;

	public static synchronized ThreadDisplaySWT getInstance() {
		if (displayThread == null) {
			displayThread = new ThreadDisplaySWT();
			displayThread.setName("SWT_ReadAndDispatch_Thread");
		}
		if (!displayThread.isStarted) {
			displayThread.start();
			displayThread.isStarted = true;
		}
		return displayThread;
	}

	public synchronized Display getDisplay() {
		if (!isStarted) {
			this.start();
			isStarted = true;
		}
		while (display == null) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return display;
	}

	// private static boolean clearingMessages = false;
	// public static void clearMessages() {
	// clearingMessages = true;
	// try {
	// Thread.sleep(3000);
	// } catch (InterruptedException e) {
	// throw new RuntimeException(e);
	// }
	// clearingMessages = false;
	// }
}

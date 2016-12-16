package com.cic.datacrawl.core.browser.tools;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;

public class UIOperation implements Runnable {
	private Thread t;
	private Runnable runnable;
	private AbstractJavaWebBrowser browser;
	private boolean isSynchronized = true;

	public UIOperation(String name, AbstractJavaWebBrowser browser, Runnable runnable) {
		super();
		t = new Thread(this);

		t.setName(name);
		this.runnable = runnable;
		this.browser = browser;
	}

	private void execute(boolean isSynchronized) {
		if (runnable != null) {
			this.isSynchronized = isSynchronized;
			if (!t.isAlive()) {
				if (t.getState().equals(Thread.State.TERMINATED)) {
					String name = t.getName();
					t = new Thread(this);
					t.setName(name);
				}
				t.start();

			}
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return t.getName();
	}

	public void syncExec() {
		execute(true);
	}

	public void asyncExec() {
		execute(false);
	}

	@Override
	public void run() {
		if (isSynchronized)
			browser.syncExec(runnable);
		else
			browser.asyncExec(runnable);
	}

}

package com.cic.datacrawl.core.rhino.shell;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

public class Runner implements Runnable, ContextAction {

	public Runner(Scriptable scope, Function func, Object[] args) {
		this.scope = scope;
		f = func;
		this.args = args;
	}

	public Runner(Scriptable scope, Script script) {
		this.scope = scope;
		s = script;
	}

	public void run() {
		running = true;
		while (running) {
			if (getExecuteCount() > Integer.MIN_VALUE)
				--executeCount;
			if (getExecuteCount() == 0)
				running = false;
			if (getWaitTime() > 0) {
				try {
					Thread.sleep(getWaitTime());
				} catch (InterruptedException e) {
				}
			}
			factory.call(this);
		}
	}

	public Object run(Context cx) {

		if (f != null)
			return f.call(cx, scope, scope, args);
		else
			return s.exec(cx, scope);

	}

	public void stop() {
		running = false;
	}

	private boolean running;

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	private ContextFactory factory;

	/**
	 * @return the factory
	 */
	public ContextFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory
	 *            the factory to set
	 */
	public void setFactory(ContextFactory factory) {
		this.factory = factory;
	}

	private int executeCount = Integer.MIN_VALUE;
	private long waitTime = 0;

	/**
	 * @return the executeCount
	 */
	public int getExecuteCount() {
		return executeCount;
	}

	/**
	 * @param executeCount
	 *            the executeCount to set
	 */
	public void setExecuteCount(int executeCount) {
		this.executeCount = executeCount;
	}

	/**
	 * @return the waitTime
	 */
	public long getWaitTime() {
		return waitTime;
	}

	/**
	 * @param waitTime
	 *            the waitTime to set
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	private Scriptable scope;
	private Function f;
	private Script s;
	private Object[] args;
}

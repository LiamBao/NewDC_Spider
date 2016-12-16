package com.cic.datacrawl.core.initialize;

import java.util.ArrayList;
import java.util.List;

public class InitializerRegister {
	private static InitializerRegister instance = new InitializerRegister();
	private List<InitializeAble> initializerList = new ArrayList<InitializeAble>();
	private boolean notFinished = true;

	/**
	 * @return the finished
	 */
	public boolean isNotFinished() {
		return notFinished;
	}

	public void register(InitializeAble initializer) {
		initializerList.add(initializer);
	}

	public static InitializerRegister getInstance() {
		return instance;
	}

	public void execute() {
		for (int i = 0; i < initializerList.size(); ++i) {
			initializerList.get(i).execute();
		}
		notFinished = false;
	}
}

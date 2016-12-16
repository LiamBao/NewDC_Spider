package com.cic.datacrawl.core.initialize;

public abstract class InitializeAble {
	public InitializeAble() {
		InitializerRegister.getInstance().register(this);
	}

	public abstract void execute();
}

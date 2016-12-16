package com.cic.datacrawl.core.rhino;

import java.util.ArrayList;
import java.util.List;

public class RhinoContextGlobalRegister {
	private static RhinoContextGlobalRegister instance = new RhinoContextGlobalRegister();

	private RhinoContextGlobalRegister() {

	}

	public static RhinoContextGlobalRegister getInstance() {
		return instance;
	}

	public void registFunction(String[] names, Class clazz) {
		functionList.add(new GlobalFunctionDefination(names, clazz));
	}

	public void registFunction(String[] names, Class clazz, int attribute) {
		functionList.add(new GlobalFunctionDefination(names, clazz, attribute));
	}

	private List<GlobalFunctionDefination> functionList = new ArrayList<GlobalFunctionDefination>();

	private List<GlobalParameterDefination> paramList = new ArrayList<GlobalParameterDefination>();

	public void registParameter(String name, Object instance) {
		paramList.add(new GlobalParameterDefination(name, instance));
	}

	public GlobalParameterDefination[] getAllParameterDefinations() {
		GlobalParameterDefination[] ret = new GlobalParameterDefination[paramList
				.size()];
		paramList.toArray(ret);
		return ret;
	}

	public GlobalFunctionDefination[] getAllFunctionDefinations() {
		GlobalFunctionDefination[] ret = new GlobalFunctionDefination[functionList
				.size()];
		functionList.toArray(ret);
		return ret;

	}
}

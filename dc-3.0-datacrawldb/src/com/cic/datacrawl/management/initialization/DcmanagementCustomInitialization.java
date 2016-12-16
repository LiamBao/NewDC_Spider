package com.cic.datacrawl.management.initialization;

import com.cic.datacrawl.core.initialize.InitializeAble;
import com.cic.datacrawl.core.rhino.RhinoContextGlobalRegister;
import com.cic.datacrawl.management.rhino.RhinoDCmanagementControlFunction;

public class DcmanagementCustomInitialization extends InitializeAble {

	@Override
	public void execute() {
		RhinoContextGlobalRegister.getInstance().registFunction(
				new String[] {  "getKey","changeTaskStatus"/*,"error", "finished", "running", "unknowError" */},
				RhinoDCmanagementControlFunction.class);
	}
}

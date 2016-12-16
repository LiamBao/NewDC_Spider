package com.cic.datacrawl.ui.shell;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public abstract class SecurityProxy extends
		org.mozilla.javascript.tools.shell.SecurityProxy {
	public void doCallProcessFileSecure(Context cx, Scriptable scope,
			String filename) {
		callProcessFileSecure(cx, scope, filename);
	}
}

package org.mozilla.javascript;

public class NativeFunctionUtil {
	public static String toString(NativeFunction function) {
		StringBuffer sb = new StringBuffer(function.toString()+"\t"
				+ function.getFunctionName() + "(");
		int paramCount = function.getParamCount();
		if (paramCount > 0) {
			for (int i = 0; i < paramCount; ++i) {
				if (i > 0)
					sb.append(", ");
				sb.append(function.getParamOrVarName(i));
			}
		}
		sb.append(")");

		return sb.toString();
	}

}

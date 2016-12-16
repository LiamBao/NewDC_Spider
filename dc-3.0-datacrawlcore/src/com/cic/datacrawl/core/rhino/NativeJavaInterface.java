package com.cic.datacrawl.core.rhino;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * Java对象转换为Rhino对象，且仅公开javaInterfaceClass所声明的接口函数
 */
public class NativeJavaInterface extends NativeJavaObject {
  
  /**
   * 
   * @param scope
   * @param javaObject 相应的java对象
   * @param javaInterfaceClass 待公开的java接口
   */
  public NativeJavaInterface(Scriptable scope, Object javaObject,
      Class<?> javaInterfaceClass) {
    super(scope, null, javaInterfaceClass);
    super.javaObject = javaObject;
  }

}

package com.cic.datacrawl.core.rpc;

import org.apache.hadoop.io.Writable;

/**
 * 业务实现接口，
 * 参数为RPC Protocol穿传递的类型
 *
 */
public interface ExecuteInterface {
	
	public Writable execute(Writable args);
	
}

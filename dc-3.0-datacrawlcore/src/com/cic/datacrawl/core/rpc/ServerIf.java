package com.cic.datacrawl.core.rpc;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.VersionedProtocol;

/**
 *	被远程访问的类，也就是Server端，
 *  必须实现VersionedProtocol接口，
 *  这个接口只有一个方法getProtocolVersion,(请参见本接口的实现类ServerImpl)
 *  用来判断Server和Client端调用的是不是一个版本的，
 *  一般Server的代码修改一次，版本号就得改一次。 
 *
 */
public interface ServerIf extends VersionedProtocol{
	
	public static final long versionID = 1l;
	
	public Writable execute(String name,Writable args)throws Exception;
	
}

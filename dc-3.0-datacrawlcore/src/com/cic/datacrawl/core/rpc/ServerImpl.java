package com.cic.datacrawl.core.rpc;

import java.io.IOException;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.ProtocolSignature;

import com.cic.datacrawl.core.ApplicationContext;

/**
 * Protocol实现类
 * 实现VersionedProtocol接口的getProtocolVersion方法
 *
 */
public class ServerImpl implements ServerIf{

	@Override
	public long getProtocolVersion(String arg0, long arg1) throws IOException {
		//直接写死，版本号一致
		return ServerIf.versionID;
	}

	/**
	 * 调用具体的实现类去处理相应的业务
	 */
	@Override
	public Writable execute(String name, Writable params) throws Exception{
		ExecuteInterface exeuter = (ExecuteInterface) ApplicationContext.getInstance().getBean(name);
		return exeuter.execute(params);
	}

	@Override
	public ProtocolSignature getProtocolSignature(String arg0, long arg1, int arg2) throws IOException {
		return new ProtocolSignature(ServerIf.versionID, null);
	}

}

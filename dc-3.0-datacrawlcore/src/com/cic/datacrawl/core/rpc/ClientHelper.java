package com.cic.datacrawl.core.rpc;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacrawl.core.rpc.protocol.FeedBack;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;


public class ClientHelper {

	private static final Logger log = Logger.getLogger(ClientHelper.class);

	private ClientImpl client;

	public ClientHelper(ClientImpl client) {
		this.client = client;
	}

	/**
	 * 调用ServerRPC服务
	 * 
	 * @param writable
	 * @param serviceBeanName
	 * @param client
	 * @return
	 */
	public FeedBack invoke(Writable writable, ServiceName serviceName) {

		FeedBack feedBack = new FeedBack(serviceName.getName());
		try {
			FeedBackWritable object = (FeedBackWritable) this.client.execute_proxy(serviceName.getName(), writable);
			feedBack.setCode(object.getCode());
			feedBack.setErrorMessage(object.getErrorMessage());
		} catch (Exception e) {
			feedBack.setCode(CodeStatus.netErrCode);// 1代表网络出问题
			feedBack.setErrorMessage("在注册的过程中，网络出现故障：" + e.getMessage());
			log.error("invoke server rpc error", e);
		}
		return feedBack;
	}
}

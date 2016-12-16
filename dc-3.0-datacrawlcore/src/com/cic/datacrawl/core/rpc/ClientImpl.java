package com.cic.datacrawl.core.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC;
import org.apache.log4j.Logger;

/**
 * _Hadoop的RPC协议实现
 * 用于将信息，发送到指定端
 *
 */
public class ClientImpl {
	private String address;//IP信息
	private int port;//端口信息
	private ServerIf serverInterface;
	
	public ClientImpl(String ip, int port){
		this.address = ip;
		this.port = port;
		
		InetSocketAddress socketAddress = new InetSocketAddress(this.address,this.port);
		try {
			serverInterface =(ServerIf)RPC.getProxy(ServerIf.class, ServerIf.versionID, socketAddress, new Configuration());
		} catch (IOException e) {
			serverInterface = null;
			e.printStackTrace();
		}	
	}
	
	public void close() {
		if(serverInterface != null) {
			RPC.stopProxy(serverInterface);
			serverInterface = null;
		}
	}
	
	public boolean isConnected(){
		return serverInterface != null;
	}
	
	private void newClient() throws IOException {
		InetSocketAddress socketAddress = new InetSocketAddress(this.address,this.port);
		serverInterface =(ServerIf)RPC.getProxy(ServerIf.class, ServerIf.versionID, socketAddress, new Configuration());
	}
	/**
	 * 重连
	 * @return
	 */
	public boolean retryToConnectServer(){
		try {
			newClient();
		} catch (IOException e) {
			e.printStackTrace();
			serverInterface = null;
		}
		return serverInterface != null; 
	}
	
	/**
	 * 
	 * @param name
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Writable execute_proxy(String name, Writable params){
		Writable writable = null;
		try {
			if(serverInterface == null) {
				newClient();
			}
			writable = serverInterface.execute(name, params);
		} catch (Exception e) {
			close();
			e.printStackTrace();
		}
		
		return writable;
	}

}

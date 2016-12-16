package com.cic.datacollection.mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.cic.datacollection.util.IPUtil;


public class MinaServerListener extends Thread {
	private static final Logger logger = Logger.getLogger(MinaServerListener.class);
	private String serverAddress;
	private int serverPort;
	private int handlerNum;
	
	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	public int getHandlerNum() {
		return handlerNum;
	}

	public void setHandlerNum(int handlerNum) {
		this.handlerNum = handlerNum;
	}

	public MinaServerListener(int serverPort, int handlerNum) {
		this.serverPort = serverPort;
		this.serverAddress = IPUtil.getHostIP();
		this.handlerNum = handlerNum;
	}
	
	public MinaServerListener(String serverAddress, int serverPort, int handlerNum) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.handlerNum = handlerNum;
	}
	
	@Override
	public void run() {
        IoAcceptor acceptor = new NioSocketAcceptor(handlerNum);
        
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());  
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));  
  
        acceptor.setHandler(new ServerIoHandlerAdapter());
          
        try {  
            acceptor.bind(new InetSocketAddress(serverAddress, serverPort));  
        } catch (IOException ex) {  
            logger.error(ex.getMessage(), ex);  
        }  
	}
}

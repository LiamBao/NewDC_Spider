package com.cic.datacollection.mina;

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * Mina Client 封装，每个MinaClient对象只能由一个线程操作，返回数据的读取封装是非线程安全的
 * @author johnney.bu
 *
 */
public class MinaClient {
	private static final Logger log = Logger.getLogger(MinaClient.class);
	
    private SocketConnector connector;
    private ConnectFuture future;
    private IoSession session;
    private ClientIoHandlerAdapter handler;
    
    private String serverAddress;
    private int serverPort;
 
	public MinaClient(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		
   	 	// 创建一个socket连接
        connector = new NioSocketConnector();
        // 设置链接超时时间
        connector.setConnectTimeoutMillis(10000);
        // 获取过滤器链
        DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
        // 添加编码过滤器 处理乱码、编码问题
        filterChain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
    }
    
    public MinaClient(String serverAddress, int serverPort, ClientIoHandlerAdapter handler) {
    	this.serverAddress = serverAddress;
		this.serverPort = serverPort;
        this.handler = handler;
        
    	// 创建一个socket连接
        connector = new NioSocketConnector();
        // 设置链接超时时间
        connector.setConnectTimeoutMillis(10000);
        // 获取过滤器链
        DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
        // 添加编码过滤器 处理乱码、编码问题
        filterChain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
 
        // 设置消息核心处理器
        connector.setHandler(handler);
    }
        
    /**
     * 连接server
     * @return
     */
    public boolean connect() {
        try {
	        // 连接服务器，知道端口、地址
	        future = connector.connect(new InetSocketAddress(serverAddress, serverPort));
	        // 等待连接创建完成
	        if(future.awaitUninterruptibly(10000)){
	        	// 获取当前session
	        	session = future.getSession();
	        }
        } catch (RuntimeIoException e) {
        	log.error(e.getMessage());
        	log.error("Cannot connect to " + serverAddress + ":" + serverPort);
        	return false;
        }
        
        return true;
    }
 
    /**
     * 重连Server，最多重试10次
     * @return
     */
    public boolean reConnect() {
    	int reTryNum = 0;
    	while(true) {
	    	try {
		    	future = connector.connect(new InetSocketAddress(serverAddress, serverPort));
		        // 等待连接创建完成
		    	if(future.awaitUninterruptibly(1000))
		    	{
		    		// 获取当前session
			        session = future.getSession();
			        return true;
		    	}
	    	} catch (RuntimeIoException e) {
	    		log.error(e.getMessage());
	    	}
	    	
	    	++reTryNum;
	    	
	    	if(reTryNum < 10) {
	    		log.error("Retry connect to server : " + serverAddress + ":" + serverPort + " " + reTryNum + " times");
	    	} else {
	    		return false;
	    	}
    	}
    }
    
    public void setAttribute(Object key, Object value) {
        session.setAttribute(key, value);
    }
 
    /**
     * 执行远程server上的服务（模拟RPC）
     * @param message
     * @return
     */
    public Object executeRemoteService(Serializable message) {
    	if(send(message)) {
    		return receive();
    	}
    	
    	return null;
    }
    
    /**
     * 发送消息给Server
     * @param message
     * @return
     */
    public boolean send(Serializable message) {
    	if(!isActive()) {
    		if(!reConnect()) {
    			return false;
    		}
    	}
    	
    	if(session != null) {
    		try{
    			session.write(message);
    		} catch (RuntimeIoException e) {
    			e.printStackTrace();
    			return false;
    		}
	        
	        return true;
    	} 
    	
    	return false;
    }
 
    /**
     * 读取server返回的内容
     * @return
     */
    public Object receive() {
    	int reTryNum = 0;
    	while(!isRecvAvailable()) {
    		++reTryNum;
    		if(reTryNum > 10) {
    			break;
    		}
    		
    		try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}

    	Object value = null;
    	value = handler.getReceiveObject();
    	handler.setReadAvailable(false);
    	
    	return value;
    }
    
    /**
     * 判断数据是否可读
     * @return
     */
    public boolean isRecvAvailable() {
    	return handler.isReadAvailable();
    }
    
    /**
     * 判断连接是否活跃
     * @return
     */
    public boolean isActive() {
    	if(session != null) {
    		return session.isConnected();
    	} else {
    		return false;
    	}
    }
    
    /**
     * 关闭连接
     * @return
     */
    public boolean close() {
    	try {
	        CloseFuture future = session.getCloseFuture();
	        future.awaitUninterruptibly(1000);
	        connector.dispose();
    	} catch (RuntimeIoException e) {
    		e.printStackTrace();
    		return false;
    	}
    	
        return true;
    }
 
    public SocketConnector getConnector() {
        return connector;
    }
 
    public IoSession getSession() {
        return session;
    }
    
    public ClientIoHandlerAdapter getHandler() {
 		return handler;
 	}

 	public void setHandler(ClientIoHandlerAdapter handler) {
 		this.handler = handler;
 		connector.setHandler(handler);
 	}
}

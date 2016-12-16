package com.cic.datacollection.mina;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Client 公共接口  handlerAdapter
 * @author johnney.bu
 *
 */

public class ClientIoHandlerAdapter extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(ClientIoHandlerAdapter.class);

	private boolean readAvailable;
	private Object receiveObject;
	
	public ClientIoHandlerAdapter() {
		readAvailable = false;
		receiveObject = null;
	}
	
	public boolean isReadAvailable() {
		return readAvailable;
	}

	public void setReadAvailable(boolean readAvailable) {
		this.readAvailable = readAvailable;
	}

	public Object getReceiveObject() {
		return receiveObject;
	}

	public void setReceiveObject(Object receiveObject) {
		this.receiveObject = receiveObject;
	}
	
	@Override  
	public void sessionCreated(IoSession session) throws Exception {
	}  
	
	@Override  
	public void sessionOpened(IoSession session) throws Exception {  
	    logger.info("Create new session");  
	}  
	
	@Override  
	public void sessionClosed(IoSession session) throws Exception {  
	}  
	
	@Override  
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {  
	}  
	
	@Override  
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {  
	    logger.error(cause.getMessage(), cause);  
	    session.close(true);  
	}  
	
	@Override  
	public void messageReceived(IoSession session, Object message) throws Exception {  
	    logger.info("Received: " + message); 
	    setReceiveObject(message);
	    setReadAvailable(true);
	}  
	
	@Override  
	public void messageSent(IoSession session, Object message) throws Exception {  
	    logger.info("Sent: " + message); 
	    setReadAvailable(false);
	    setReceiveObject(null);
	}
}

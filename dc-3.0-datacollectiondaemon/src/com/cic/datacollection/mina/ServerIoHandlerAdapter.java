package com.cic.datacollection.mina;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.cic.datacrawl.core.ApplicationContext;

public class ServerIoHandlerAdapter extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(ServerIoHandlerAdapter.class);
	
	@Override  
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {  
        logger.error(cause.getMessage(), cause);  
        session.close(true);  
    }  

    @Override  
    public void messageReceived(IoSession session, Object message) throws Exception {
    	MinaRequestProtocol requestObj = (MinaRequestProtocol)message;
    	logger.info("Receive Request Protocol Name: " + requestObj.getProtocolName());
    	MinaExecuteInterface minaExecutor = (MinaExecuteInterface)ApplicationContext.getInstance().getBean(requestObj.getProtocolName());
    	MinaResponseProtocol responseObj = (MinaResponseProtocol)minaExecutor.execute(message);
    	session.write(responseObj);
    }
    
	@Override  
	public void messageSent(IoSession session, Object message) throws Exception {  
	    logger.info("Sent: " + message); 
	}
}

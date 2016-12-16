package com.cic.datacrawl.core.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * 抓取记录打包协议
 * @author charles.chen
 *
 */
public class RecordCollectWritable implements Writable {
	public String entityTypeName;
	/**
	 * 记录数据
	 */
	public String data;
	public String businessName;
	
	public RecordCollectWritable(){
		
	}
	

	public RecordCollectWritable(String entityTypeName, String jsons,String businessName) {
		if(entityTypeName == null){
			this.entityTypeName = "";
		} else {
			this.entityTypeName = entityTypeName;
		}
		
		if(jsons == null){
			this.data = "";
		} else {
			this.data = jsons;
		}
		
		if(businessName == null){
			this.businessName = "";
		} else {
			this.businessName = businessName;
		}
	}



	@Override
	public void readFields(DataInput out) throws IOException {
		entityTypeName = out.readUTF();
		data = out.readUTF();
		businessName = out.readUTF();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeUTF(entityTypeName);
		in.writeUTF(data);
		in.writeUTF(businessName);
	}
	
	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("RecordCollectWritable: [entityTypeName: " + entityTypeName + ",data:" + data +",businessName:" + businessName + "]");  
        return strBuf.toString(); 
	}
}


package com.cic.datacollection.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.Writable;

public class ResAgentCheckConsistencyWritable implements Writable {
	public int code;
	public int num;
	public long[] subTaskIds;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setSubTaskIds(List<Long> subTaskIds) {
		if(subTaskIds.isEmpty()) {
			this.subTaskIds = new long[1];
		} else {
			this.subTaskIds = new long[subTaskIds.size()];
		}
		
		this.num = 0;
		if(!subTaskIds.isEmpty()) {
			for(Long id : subTaskIds){
				this.subTaskIds[this.num] = id;
				++(this.num);
			}
		}
	}
	
	public long[] getSubTaskIds() {
		return subTaskIds;
	}
	
	public ResAgentCheckConsistencyWritable() {
	}
	
	@Override
	public void readFields(DataInput out) throws IOException {
		code = out.readInt();
		num = out.readInt();
		if(num > 0) {
			subTaskIds = new long[num];
		}
		
		for(int i=0; i<num; ++i) {
			subTaskIds[i] = out.readLong();
		}
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(code);
		in.writeInt(num);
		for(int i=0; i<num; ++i) {
			in.writeLong(subTaskIds[i]);
		}
	}

	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();
		String strSubTaskIds = "{";
		for(int i=0; i<num; ++i) {
			strSubTaskIds += subTaskIds[i];
			if(i < num-1) {
				strSubTaskIds += ",";
			}
		}
		strSubTaskIds += "}";
		
		strBuf.append("ResCheckConsistency: [code: " + code + ",num: " + num + ",subTaskIds: " + strSubTaskIds + "]");  
        return strBuf.toString(); 
	}
}

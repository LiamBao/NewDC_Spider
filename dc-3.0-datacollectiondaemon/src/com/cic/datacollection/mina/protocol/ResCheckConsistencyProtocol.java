package com.cic.datacollection.mina.protocol;

import java.io.Serializable;
import java.util.List;

import com.cic.datacollection.mina.MinaResponseProtocol;

public class ResCheckConsistencyProtocol extends MinaResponseProtocol implements Serializable {
	private static final long serialVersionUID = 6176637980859957088L;
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
		this.subTaskIds = new long[subTaskIds.size()];
		
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

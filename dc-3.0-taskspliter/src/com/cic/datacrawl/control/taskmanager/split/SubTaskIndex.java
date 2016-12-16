package com.cic.datacrawl.control.taskmanager.split;

import com.cic.datacrawl.management.entity.SubTaskEntity;

/**
 * 该类用来保存SubTask以及对应的位置关系，将来根据位置来保存拆除来的subtask顺序一直
 */
public class SubTaskIndex {
	
	public SubTaskEntity subTask;
	public int index;
	
	public SubTaskIndex(){
		
	}
	
	public SubTaskIndex(SubTaskEntity subTask, int index){
		this.subTask = subTask;
		this.index = index;
	}

	public SubTaskEntity getSubTask() {
		return subTask;
	}

	public void setSubTask(SubTaskEntity subTask) {
		this.subTask = subTask;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}

package com.cic.datacrawl.core.entity;

import org.apache.log4j.Logger;

public abstract class EntitySaveManager {
	public static final Logger LOG = Logger.getLogger(EntitySaveManager.class);
	public static final int DEFUALT_BUFFERED_SIZE = 100;
	// 缓存保存数据大小
	public static final int DEFUALT_BUFFERED_LENGTH = 39000;
	public abstract void save(BaseEntity entity);

	public abstract void save(BaseEntity[] entities);

	public abstract void commit() throws Exception;

	public abstract void clean();
	
	protected int totalElementCount = 0;

	public int getTotalElementCount() {
		return totalElementCount;
	}
	
	private int bufferSize = DEFUALT_BUFFERED_SIZE;

	/**
	 * @return the bufferSize
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param bufferSize
	 *            the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

}

package com.cic.datacollection.util;

import java.io.File;
import java.net.URI;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockFile extends File {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static WeakHashMap<String, ReentrantReadWriteLock> locks = new WeakHashMap<String, ReentrantReadWriteLock>();
	private ReentrantReadWriteLock lock = null;

	public LockFile(File arg0, String arg1) {
		
		super(arg0, arg1);
		lock = initLock(this.getAbsolutePath());
	}

	public LockFile(String arg0, String arg1) {
		super(arg0, arg1);
		lock = initLock(this.getAbsolutePath());
	}

	public LockFile(String arg0) {
		super(arg0);
		lock = initLock(this.getAbsolutePath());
	}

	public LockFile(URI arg0) {
		super(arg0);
		lock = initLock(this.getAbsolutePath());
	}

	/*
	 * 这里要注意使用 static synchronized，不然可能同时有多个进行初始化这个文件
	 */
	private static synchronized ReentrantReadWriteLock initLock(String path) {
		ReentrantReadWriteLock lock = locks.get(path);
		if (lock == null) {
			lock = new ReentrantReadWriteLock();
			locks.put(path, lock);
		}
		return lock;
	}

	public ReentrantReadWriteLock getLock() {
		return lock;
	}
}

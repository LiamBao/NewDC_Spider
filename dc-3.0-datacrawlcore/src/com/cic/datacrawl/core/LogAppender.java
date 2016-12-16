package com.cic.datacrawl.core;

import java.io.File;
import java.io.Writer;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.RollingFileAppender;

import com.cic.datacrawl.core.config.Config;

public class LogAppender extends AppenderSkeleton {
	
	/**
	 * Instantiate a RollingFileAppender and open the file designated by
	 * <code>filename</code>. The opened filename will become the ouput
	 * destination for this appender.
	 * 
	 * <p>
	 * If the <code>append</code> parameter is true, the file will be appended
	 * to. Otherwise, the file desginated by <code>filename</code> will be
	 * truncated before being opened.
	 */
	public LogAppender() {
		super();
		initAppender();
	}

	private RollingFileAppender infoAppender;
	private RollingFileAppender debugAppender;
	private RollingFileAppender warnAppender;
	private RollingFileAppender errorAppender;
	
	private boolean append;
	private String encoding;
	private int bufferSize = 8 * 1024;
	private boolean immediateFlush;
	private int maxBackupIndex;
	private String maxFileSize;
	private long maximumFileSize;
	private Writer writer;
	private boolean bufferedIO;
	private long processID;

	private static final int DEFAULT_MAX_BACKUP_INDEX = 10000;

	private void initAppender() {
		if (infoAppender == null) {
			infoAppender = new RollingFileAppender();
			infoAppender.setMaxBackupIndex(DEFAULT_MAX_BACKUP_INDEX);
		}
		if (debugAppender == null) {
			debugAppender = new RollingFileAppender();
			debugAppender.setMaxBackupIndex(DEFAULT_MAX_BACKUP_INDEX);
		}
		if (warnAppender == null) {
			warnAppender = new RollingFileAppender();
			warnAppender.setMaxBackupIndex(DEFAULT_MAX_BACKUP_INDEX);
		}
		if (errorAppender == null) {
			errorAppender = new RollingFileAppender();
			errorAppender.setMaxBackupIndex(DEFAULT_MAX_BACKUP_INDEX);
		}
		initFile();
	}

	public int getMaxBackupIndex() {
		return maxBackupIndex;
	}

	public long getMaximumFileSize() {
		return maximumFileSize;
	}

	public boolean getAppend() {
		return append;
	}

	public boolean getBufferedIO() {
		return bufferedIO;
	}

	public boolean getImmediateFlush() {
		return immediateFlush;
	}

	@Override
	public boolean isAsSevereAsThreshold(org.apache.log4j.Priority priority) {
		return super.isAsSevereAsThreshold(priority);
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public String getEncoding() {
		return encoding;
	}

	@Override
	public org.apache.log4j.Layout getLayout() {
		return super.getLayout();
	}

	@Override
	public org.apache.log4j.Priority getThreshold() {
		return super.getThreshold();
	}

	@Override
	public org.apache.log4j.spi.ErrorHandler getErrorHandler() {
		return super.getErrorHandler();
	}

	@Override
	public org.apache.log4j.spi.Filter getFilter() {
		return super.getFilter();
	}

	@Override
	public void activateOptions() {
		super.activateOptions();
		infoAppender.activateOptions();
		debugAppender.activateOptions();
		warnAppender.activateOptions();
		errorAppender.activateOptions();
	}

	@Override
	public void addFilter(org.apache.log4j.spi.Filter filter) {
		super.addFilter(filter);
		infoAppender.addFilter(filter);
		debugAppender.addFilter(filter);
		warnAppender.addFilter(filter);
		errorAppender.addFilter(filter);
	}

	/**
	 * 这个方法的子类来执行实际的日志记录。
	 */
	@Override
	public void append(org.apache.log4j.spi.LoggingEvent event) {
		int intLevelValue = event.getLevel().toInt();
		switch (intLevelValue) {
		case Level.ALL_INT:
			debugAppender.append(event);
			infoAppender.append(event);
			warnAppender.append(event);
			errorAppender.append(event);
			break;
		case Level.INFO_INT:
			infoAppender.append(event);
			break;
		case Level.DEBUG_INT:
			debugAppender.append(event);
			break;
		case Level.WARN_INT:
			warnAppender.append(event);
			break;
		case Level.ERROR_INT:
			errorAppender.append(event);
			break;

		default:
			if (intLevelValue < Level.OFF_INT && intLevelValue > Level.ERROR_INT) {
				errorAppender.append(event);
			} else if (intLevelValue < Level.ERROR_INT && intLevelValue > Level.WARN_INT) {
				warnAppender.append(event);
			} else if (intLevelValue < Level.WARN_INT && intLevelValue > Level.INFO_INT) {
				infoAppender.append(event);
			} else if (intLevelValue < Level.INFO_INT && intLevelValue > Level.ALL_INT) {
				debugAppender.append(event);
			}
		}

	}

	@Override
	public void clearFilters() {
		super.clearFilters();
		infoAppender.clearFilters();
		debugAppender.clearFilters();
		warnAppender.clearFilters();
		errorAppender.clearFilters();
	}

	@Override
	public void close() {
		infoAppender.close();
		debugAppender.close();
		warnAppender.close();
		errorAppender.close();
	}

	/**
	 * 此方法执行阈值检查,并调用过滤器之前委托实际记录具体的子类
	 */
	@Override
	public void doAppend(org.apache.log4j.spi.LoggingEvent event) {
		//super.doAppend(event);
		int intLevelValue = event.getLevel().toInt();
		switch (intLevelValue) {
		case Level.ALL_INT:
			debugAppender.doAppend(event);
			infoAppender.doAppend(event);
			warnAppender.doAppend(event);
			errorAppender.doAppend(event);
			break;
		case Level.INFO_INT:
			infoAppender.doAppend(event);
			break;
		case Level.DEBUG_INT:
			debugAppender.doAppend(event);
			break;
		case Level.WARN_INT:
			warnAppender.doAppend(event);
			break;
		case Level.ERROR_INT:
			errorAppender.doAppend(event);
			break;

		default:
			if (intLevelValue < Level.OFF_INT && intLevelValue > Level.ERROR_INT) {
				errorAppender.doAppend(event);
			} else if (intLevelValue < Level.ERROR_INT && intLevelValue > Level.WARN_INT) {
				warnAppender.doAppend(event);
			} else if (intLevelValue < Level.WARN_INT && intLevelValue > Level.INFO_INT) {
				infoAppender.doAppend(event);
			} else if (intLevelValue < Level.INFO_INT && intLevelValue > Level.ALL_INT) {
				debugAppender.doAppend(event);
			}
			break;
		}
	}

	@Override
	public void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
		}
		infoAppender.finalize();
		debugAppender.finalize();
		warnAppender.finalize();
		errorAppender.finalize();
	}

	public void rollOver() {
		infoAppender.rollOver();
		debugAppender.rollOver();
		warnAppender.rollOver();
		errorAppender.rollOver();
	}

	public void setAppend(boolean append) {
		this.append = append;
		infoAppender.setAppend(append);
		debugAppender.setAppend(append);
		warnAppender.setAppend(append);
		errorAppender.setAppend(append);
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		infoAppender.setBufferSize(bufferSize);
		debugAppender.setBufferSize(bufferSize);
		warnAppender.setBufferSize(bufferSize);
		errorAppender.setBufferSize(bufferSize);
	}

	public void setBufferedIO(boolean bufferedIO) {
		this.bufferedIO = bufferedIO;
		infoAppender.setBufferedIO(bufferedIO);
		debugAppender.setBufferedIO(bufferedIO);
		warnAppender.setBufferedIO(bufferedIO);
		errorAppender.setBufferedIO(bufferedIO);
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
		infoAppender.setEncoding(encoding);
		debugAppender.setEncoding(encoding);
		warnAppender.setEncoding(encoding);
		errorAppender.setEncoding(encoding);
	}

	@Override
	public void setErrorHandler(org.apache.log4j.spi.ErrorHandler errorHandler) {
		super.setErrorHandler(errorHandler);
		infoAppender.setErrorHandler(errorHandler);
		debugAppender.setErrorHandler(errorHandler);
		warnAppender.setErrorHandler(errorHandler);
		errorAppender.setErrorHandler(errorHandler);
	}

	private void initFile() {

		infoAppender.setFile(Config.getLogFilePath() + File.separator + "info.log");
		debugAppender.setFile(Config.getLogFilePath() + File.separator + "debug.log");
		warnAppender.setFile(Config.getLogFilePath() + File.separator + "warn.log");
		errorAppender.setFile(Config.getLogFilePath() + File.separator + "error.log");
	}

	public void setImmediateFlush(boolean immediateFlush) {
		this.immediateFlush = immediateFlush;
		infoAppender.setImmediateFlush(immediateFlush);
		debugAppender.setImmediateFlush(immediateFlush);
		warnAppender.setImmediateFlush(immediateFlush);
		errorAppender.setImmediateFlush(immediateFlush);
	}

	@Override
	public void setLayout(org.apache.log4j.Layout layout) {
		super.setLayout(layout);
		infoAppender.setLayout(layout);
		debugAppender.setLayout(layout);
		warnAppender.setLayout(layout);
		errorAppender.setLayout(layout);
	}

	public void setMaxBackupIndex(int maxBackupIndex) {
		this.maxBackupIndex = maxBackupIndex;
		infoAppender.setMaxBackupIndex(maxBackupIndex);
		debugAppender.setMaxBackupIndex(maxBackupIndex);
		warnAppender.setMaxBackupIndex(maxBackupIndex);
		errorAppender.setMaxBackupIndex(maxBackupIndex);
	}

	public void setMaxFileSize(String maxFileSize) {
		this.maxFileSize = maxFileSize;
		infoAppender.setMaxFileSize(maxFileSize);
		debugAppender.setMaxFileSize(maxFileSize);
		warnAppender.setMaxFileSize(maxFileSize);
		errorAppender.setMaxFileSize(maxFileSize);
	}

	public void setMaximumFileSize(long maximumFileSize) {
		this.maximumFileSize = maximumFileSize;
		infoAppender.setMaximumFileSize(maximumFileSize);
		debugAppender.setMaximumFileSize(maximumFileSize);
		warnAppender.setMaximumFileSize(maximumFileSize);
		errorAppender.setMaximumFileSize(maximumFileSize);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		infoAppender.setName(name + "_info");
		debugAppender.setName(name + "_debug");
		warnAppender.setName(name + "_warn");
		errorAppender.setName(name + "_error");
	}

	@Override
	public void setThreshold(org.apache.log4j.Priority priority) {
		super.setThreshold(priority);
		infoAppender.setThreshold(priority);
		debugAppender.setThreshold(priority);
		warnAppender.setThreshold(priority);
		errorAppender.setThreshold(priority);
	}

	public void setWriter(java.io.Writer writer) {
		this.writer = writer;
		infoAppender.setWriter(writer);
		debugAppender.setWriter(writer);
		warnAppender.setWriter(writer);
		errorAppender.setWriter(writer);
	}

}

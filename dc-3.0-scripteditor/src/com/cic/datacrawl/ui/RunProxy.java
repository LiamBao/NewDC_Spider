package com.cic.datacrawl.ui;

import java.sql.Timestamp;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.rhino.debugger.RhinoDim;
import com.cic.datacrawl.core.rhino.debugger.ScriptExecuterDefaultImpl;
import com.cic.datacrawl.ui.thread.RunThreadManager;
import com.cic.datacrawl.ui.utils.SwingFileUtils;

/**
 * Class to consolidate all cases that require to implement Runnable to avoid
 * class generation bloat.
 */
public class RunProxy extends ScriptExecuterDefaultImpl implements Runnable {
	private static final Logger logger = Logger.getLogger(RunProxy.class);
	// Constants for 'type'.
	public static final int OPEN_FILE = 1;
	public static final int LOAD_FILE = 2;
	public static final int UPDATE_SOURCE_TEXT = 3;
	public static final int ENTER_INTERRUPT = 4;

	/**
	 * The debugger GUI.
	 */
	private SwingGui debugGui;

	/**
	 * The file window.
	 */
	public FileWindow fileWindow;

	/**
	 * The type of Runnable this object is. Takes one of the constants defined
	 * in this class.
	 */
	private int type;

	/**
	 * The name of the file to open or load.
	 */
	private String fileName;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		setScriptName(fileName);
		this.fileName = fileName;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * The source text to update.
	 */
	private String text;

	/**
	 * The source for which to update the text.
	 */
	public RhinoDim.SourceInfo sourceInfo;

	/**
	 * The frame to interrupt in.
	 */
	public RhinoDim.StackFrame lastFrame;

	/**
	 * The name of the interrupted thread.
	 */
	public String threadTitle;

	/**
	 * The message of the exception thrown that caused the thread interruption,
	 * if any.
	 */
	public String alertMessage;

	/**
	 * Creates a new RunProxy.
	 */
	public RunProxy(SwingGui debugGui, int type) {
		this.debugGui = debugGui;
		this.type = type;
	}

	public static void executeFile(SwingGui swingGui, String filename) {
		if (filename != null) {
			String text = SwingFileUtils.readFile(filename, swingGui);
			if (text != null) {
				logger.info("RunProxy executeFile()");
				RunProxy proxy = new RunProxy(swingGui, RunProxy.LOAD_FILE);
				proxy.fileName = filename;
				proxy.text = text;
				Thread t = new Thread(proxy);
				RunThreadManager.getInstance().addThread(proxy.getExecuterName(), t);

				t.start();
			}
		}
	}

	/**
	 * Runs this Runnable.
	 */
	public void run() {
		switch (type) {
		case OPEN_FILE:
			try {
				debugGui.dim.compileScript(fileName, text);
				System.out.println(new Timestamp(System.currentTimeMillis())+": Complie complete. Filename: " + fileName);
			} catch (RuntimeException ex) {
				if (logger.isDebugEnabled()) {
					logger.debug(ex.getMessage(), ex);
				}
				
				logger.error(ex.getMessage());

				MessageDialogWrapper.showMessageDialog(debugGui, ex.getMessage(), "Error Compiling "
																					+ fileName,
														JOptionPane.ERROR_MESSAGE);
			}
			break;

		case LOAD_FILE:
			try {
				waitForExecute();
				enter();
				logger.info("RunProxy run(): LOAD_FILE");
				debugGui.dim.evalScript(fileName, text);
			} catch (JavaScriptException ex) {
				Throwable t = ex;
				boolean showAlert = true;
				while (t != null && showAlert) {
					if (t instanceof InterruptedException && t.getMessage().indexOf("sleep interrupted") >= 0) {
						showAlert = false;
					}
					t = t.getCause();
				}
				if (showAlert) {
					StringBuilder errorMessage = new StringBuilder();
					errorMessage.append(ObjectUtils.toString(ex.getValue()));
					if (ex.lineSource() != null) {
						String lineSource = ex.lineSource().trim();
						if (lineSource.length() > 0 && !lineSource.equalsIgnoreCase("NULL")) {

							errorMessage.append("\nLine Source:\n");
							errorMessage.append(lineSource);
						}
					}

					String errMessage = errorMessage.toString();
					if (logger.isDebugEnabled()) {
						logger.debug(errMessage, ex);
					}

					logger.warn(errMessage, ex);
					MessageDialogWrapper.showMessageDialog(debugGui, errMessage, "Java Script Exception",
															JOptionPane.ERROR_MESSAGE);
				}
			} catch (RuntimeException ex) {
				Throwable t = ex;
				boolean showAlert = true;
				while (t != null && showAlert) {
					if (t instanceof InterruptedException) {
						showAlert = false;
						break;
					}
					t = t.getCause();
				}
				if (showAlert) {
					if (logger.isDebugEnabled()) {
						logger.debug(ex.getMessage(), ex);
					}

					logger.warn(ex.getMessage(), ex);
					MessageDialogWrapper.showMessageDialog(debugGui, ex.getMessage() + "\n" /*
																							 * +
																							 * errorTrace
																							 */,
															"Unknow Exception", JOptionPane.ERROR_MESSAGE);
				}
			} finally {
				exit();
			}
			break;

		case UPDATE_SOURCE_TEXT: {
			String fileName = sourceInfo.url();
			if (!debugGui.updateFileWindow(sourceInfo) && !fileName.equals("<stdin>")) {
				debugGui.createFileWindow(sourceInfo, -1);
			}
		}
			break;

		case ENTER_INTERRUPT:
			debugGui.enterInterruptImpl(lastFrame, threadTitle, alertMessage);
			break;

		default:
			throw new IllegalArgumentException(String.valueOf(type));
		}
	}
}

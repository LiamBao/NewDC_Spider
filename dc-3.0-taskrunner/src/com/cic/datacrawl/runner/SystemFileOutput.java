package com.cic.datacrawl.runner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import javax.swing.SwingUtilities;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.util.StringUtil;

class OutputWrite implements Runnable {
	private SystemFileOutput out;
	private byte[] bytes;

	public OutputWrite(SystemFileOutput out, byte[] bytes) {
		this.out = out;
		this.bytes = bytes;
	}

	public void run() {
		out.write(bytes);
	}
}

class OutputWriter extends java.io.OutputStream {

	private Vector<Byte> buffer;
	private SystemFileOutput out;

	public OutputWriter(SystemFileOutput out) {
		this.out = out;
		buffer = new Vector<Byte>();
	}

	@Override
	public synchronized void write(int ch) {
		buffer.add(new Byte((byte) ch));
		if (ch == '\n') {
			flushBuffer();
		}
	}

	public synchronized void write(char[] data, int off, int len) {
		for (int i = off; i < len; i++) {
			buffer.add(new Byte((byte) data[i]));
			if (data[i] == '\n' || data[i] == '\r') {
				flushBuffer();
			}
		}
	}

	@Override
	public synchronized void flush() {
		if (buffer.size() > 0) {
			flushBuffer();
		}
	}

	@Override
	public void close() {
		flush();
	}

	private void flushBuffer() {
		byte[] bytes = new byte[buffer.size()];
		for (int i = 0; i < buffer.size(); ++i) {
			bytes[i] = buffer.get(i).byteValue();
		}

		buffer.clear();
		synchronized (out) {
			SwingUtilities.invokeLater(new OutputWrite(out, bytes));
		}

	}
}

public class SystemFileOutput {
	private OutputWriter console1;
	private PrintStream out;
	private PrintStream stdout;
	private String filename;
	private short attachFileType = 0;

	public SystemFileOutput(String filename, PrintStream stdout) {
		this.stdout = stdout;
		this.filename = filename;
		console1 = new OutputWriter(this);
		out = new PrintStream(console1, true);
	}

	public void write(byte[] bytes) {
		try {
			stdout.write(bytes);
		} catch (IOException e1) {
		}
		String str = null;
		try {
			str = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		if (str != null)
			writeToFile(StringUtil.readUTF8(str));
	}

	private void writeToFile(String str) {
		File f = new File(Config.getLogFilePath() + File.separator + filename);
		if (!f.exists()) {
			try {
				if (!f.createNewFile())
					return;
			} catch (IOException e) {
				return;
			}
		} else {
			if (f.length() > 2 * 1024 * 1024) {
				f.renameTo(new File(f.getAbsolutePath() + "." + attachFileType));
				++attachFileType;
				try {
					if (!f.createNewFile())
						return;
				} catch (IOException e) {
					return;
				}
			}
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f, true));
			writer.write(str);
		} catch (IOException e) {
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
				}
		}
	}

	public PrintStream getOut() {
		return out;
	}

}

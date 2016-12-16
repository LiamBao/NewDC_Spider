package com.cic.datacrawl.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.cic.datacrawl.core.util.StringUtil;

class Write implements Runnable {
	private OutputFile textArea;
	private String str;

	public Write(OutputFile textArea, String str) {
		this.textArea = textArea;
		this.str = str;
	}

	public void run() {
		textArea.write(str);
	}
}

class Writer extends java.io.OutputStream {

	private OutputFile textArea;
	private Vector<Byte> buffer;

	public Writer(OutputFile textArea) {
		this.textArea = textArea;
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

	//
	// @Override
	// public void write(byte b[], int off, int len) throws IOException {
	// textArea.append(new String(b, off, len));
	// textArea.setCaretPosition(textArea.getText().length());
	// }

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
		String str = null;
		try {
			str = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		buffer.clear();
		if (str != null) {
			synchronized (textArea) {
				new Thread(new Write(textArea, str)).start();
			}
		}
	}
}

public class OutputFile {
	static final long serialVersionUID = 8557083244830872961L;

	private Writer console1;
	private Writer console2;
	private PrintStream out;
	private PrintStream err;

	private File f;

	public OutputFile(String filename) {
		super();
		console1 = new Writer(this);
		console2 = new Writer(this);
		out = new PrintStream(console1, true);
		err = new PrintStream(console2, true);
		f = new File(filename);
	}

	public synchronized void write(String str) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f, true));

			writer.write(StringUtil.readUTF8(str));
			writer.newLine();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public PrintStream getOut() {
		return out;
	}

	public PrintStream getErr() {
		return err;
	}

}

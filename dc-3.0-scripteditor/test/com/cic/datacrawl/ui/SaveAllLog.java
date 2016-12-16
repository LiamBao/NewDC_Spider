package com.cic.datacrawl.ui;

public class SaveAllLog {
	public static void main(String[] args) {
		String filename = "c:/temp/output.txt";

		OutputFile out = new OutputFile(filename);
		System.setOut(out.getOut());
		System.setErr(out.getErr());

		for (int i = 0; i < 10; ++i) {
			System.out.println("----------> " + i);
		}
		System.out.println();
		System.gc();
	}
}

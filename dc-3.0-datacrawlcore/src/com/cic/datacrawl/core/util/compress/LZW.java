package com.cic.datacrawl.core.util.compress;

import java.io.IOException;

public class LZW {
	private static final int R = 256; // number of input chars
	private static final int L = 4096; // number of codewords = 2^W
	private static final int W = 12; // codeword width

	public static String compress(String input) throws IOException {
		if (input == null || input.trim().length() == 0) {
			return "";
		}
		TST<Integer> st = new TST<Integer>();
		for (int i = 0; i < R; i++)
			st.put("" + (char) i, i);
		int code = R + 1; // R is codeword for EOF
		BinaryStdOut out = new BinaryStdOut();
		String ret = "";
		try {
			while (input.length() > 0) {
				String s = st.longestPrefixOf(input); // Find max prefix match
				// s.
				out.write(st.get(s), W); // Print s's encoding.
				int t = s.length();
				if (t < input.length() && code < L) // Add s to symbol table.
					st.put(input.substring(0, t + 1), code++);
				input = input.substring(t); // Scan past s in input.
			}
			out.write(R, W);
			ret = out.getContent();
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
		return ret;
	}

	private static String expand(BinaryStdIn in) throws IOException {
		String[] st = new String[L];
		int i; // next available codeword value

		// initialize symbol table with all 1-character strings
		for (i = 0; i < R; i++)
			st[i] = "" + (char) i;
		st[i++] = ""; // (unused) lookahead for EOF

		int codeword = in.readInt(W);
		String val = st[codeword];
		BinaryStdOut out = new BinaryStdOut();
		String ret = "";
		try {
			while (true) {
				out.write(val);
				codeword = in.readInt(W);
				if (codeword == R)
					break;
				String s = st[codeword];
				if (i == codeword)
					s = val + val.charAt(0); // special case hack
				if (i < L)
					st[i++] = val + s.charAt(0);
				val = s;
			}
			ret = out.getContent();
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
			in.close();
		}
		return ret;
	}

	public static String expand(String compressStr, String charset) throws IOException {
		return expand(new BinaryStdIn(compressStr, charset));
	}

	public static String expand(String compressStr) throws IOException {
		return expand(new BinaryStdIn(compressStr));
	}

	public static void main(String[] args) throws IOException {

		String src = "aaaaaaaaaa1"
						+ "\nbbbbbbbbbb2"
						+ "\naaaaaaaaaa1"
						+ "\nbbbbbbbbbb2"
						+ "\naaaaaaaaaa1"
						+ "\nbbbbbbbbbb2"
						+ "\naaaaaaaaaa1"
						+ "\nbbbbbbbbbb2";
		String c = "";

		c = compress(src);
		System.out.println(c);
		// String b = expand(c);
		// System.out.println(b);

	}

}

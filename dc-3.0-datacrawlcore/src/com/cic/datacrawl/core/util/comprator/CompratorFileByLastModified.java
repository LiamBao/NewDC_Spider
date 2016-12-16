package com.cic.datacrawl.core.util.comprator;

import java.io.File;
import java.util.Comparator;

public class CompratorFileByLastModified implements Comparator<File> {
	public int compare(File f1, File f2) {
		long diff = f1.lastModified() - f2.lastModified();
		if (diff > 0)
			return 1;
		else if (diff == 0)
			return 0;
		else
			return -1;
	}
}

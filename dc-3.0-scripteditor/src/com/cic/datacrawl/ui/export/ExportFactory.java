package com.cic.datacrawl.ui.export;

import javax.swing.JTabbedPane;

public class ExportFactory {
	public static AbstractExport buildExport(String filetype, JTabbedPane tabbedPane) {
		if ("xls".equalsIgnoreCase(filetype)) {
			return ExportToExcel.getInstance(tabbedPane);
		} else if ("xml".equalsIgnoreCase(filetype)) {
			return ExportToXML.getInstance(tabbedPane);
		}
		throw new IllegalArgumentException("Invalid export type: " + filetype);
	}
}

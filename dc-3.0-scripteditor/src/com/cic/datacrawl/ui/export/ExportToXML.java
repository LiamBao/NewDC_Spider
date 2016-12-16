package com.cic.datacrawl.ui.export;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JTabbedPane;

import org.mozilla.javascript.NativeObjectUtil;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.FileUtils;
import com.cic.datacrawl.core.util.XMLUtil;

public class ExportToXML extends AbstractExport {
	private ExportToXML(JTabbedPane tabPanel) {
		setTabPanel(tabPanel);
	}

	private static ExportToXML instance;

	public static final ExportToXML getInstance(JTabbedPane tabPanel) {
		if (instance == null) {
			instance = new ExportToXML(tabPanel);
		}
		return instance;
	}

	@Override
	public void doExport(String filename) {
		doExport(filename, false);
	}

	@Override
	public void doExportAll(String filename) {
		doExport(filename, true);
	}

	private void doExport(String filename, boolean exportAll) {
		List<BaseEntity> entityList = getAllBaseEntities(getResultPanels(exportAll));

		String content = XMLUtil.parseXMLValue(entityList);
		try {
			File realFile = FileUtils.saveFile(filename, content, false);
			System.out.println("Export into \"" + realFile.getAbsolutePath() + "\" is finished.");
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}

	}
}

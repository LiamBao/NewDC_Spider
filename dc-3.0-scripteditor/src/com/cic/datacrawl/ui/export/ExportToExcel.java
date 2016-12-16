package com.cic.datacrawl.ui.export;

import java.io.IOException;
import java.util.List;

import javax.swing.JTabbedPane;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.ExcelUtil;
import com.cic.datacrawl.core.util.FileUtils;
import com.cic.datacrawl.ui.panel.ResultPanel;

public class ExportToExcel extends AbstractExport {
	private ExportToExcel(JTabbedPane tabPanel) {
		setTabPanel(tabPanel);
	}

	private static ExportToExcel instance;

	public static final ExportToExcel getInstance(JTabbedPane tabPanel) {
		if (instance == null) {
			instance = new ExportToExcel(tabPanel);
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

		ResultPanel[] resultPanels = getResultPanels(exportAll);
		if (resultPanels != null && resultPanels.length > 0) {
			ExcelUtil excel = null;
			try {
				filename = FileUtils.buildValidFileName(filename, false);
				excel = ExcelUtil.createNewExcel(filename, false);
			} catch (BiffException e) {
			} catch (IOException e) {
			}
			if (excel != null) {
				for (int i = 0; i < resultPanels.length; ++i) {
					List<BaseEntity> entityList = getAllBaseEntities(resultPanels[i]);
					if (entityList.size() > 0) {
						excel.addValue(entityList.get(0).getTheEntityName(), entityList);
					}
				}
				try {
					excel.save();
					System.out.println("Export into \"" + filename + "\" is finished.");
				} catch (WriteException e) {
				} catch (IOException e) {
				}
			}
		}
	}
}

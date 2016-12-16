package com.cic.datacrawl.ui.export;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.ui.panel.ResultPanel;

public abstract class AbstractExport {
	protected static final Logger LOG = Logger.getLogger(AbstractExport.class);

	public abstract void doExport(String filename);

	public abstract void doExportAll(String filename);

	public JTabbedPane tabPanel;

	/**
	 * @return the tabPanel
	 */
	protected JTabbedPane getTabPanel() {
		return tabPanel;
	}

	/**
	 * @param tabPanel
	 *            the tabPanel to set
	 */
	protected void setTabPanel(JTabbedPane tabPanel) {
		this.tabPanel = tabPanel;
	}

	protected ResultPanel[] getResultPanels(boolean exportAll) {
		ResultPanel[] panels = null;
		if (exportAll) {
			ArrayList<ResultPanel> list = new ArrayList<ResultPanel>();
			Component[] components = tabPanel.getComponents();
			for (int i = 0; i < components.length; ++i) {
				if (components[i] instanceof ResultPanel) {
					list.add((ResultPanel) components[i]);
				}
			}
			panels = new ResultPanel[list.size()];
			list.toArray(panels);
		} else {
			panels = new ResultPanel[] { (ResultPanel) tabPanel.getSelectedComponent() };

		}
		return panels;
	}

	protected List<BaseEntity> getAllBaseEntities(ResultPanel resultPanel) {
		return getAllBaseEntities(new ResultPanel[] { resultPanel });
	}

	protected List<BaseEntity> getAllBaseEntities(ResultPanel[] resultPanels) {
		if (resultPanels == null || resultPanels.length == 0) {
			return new ArrayList<BaseEntity>();
		}
		List<BaseEntity> list = new ArrayList<BaseEntity>();
		for (int i = 0; i < resultPanels.length; ++i) {
			list.addAll(resultPanels[i].getAllResults());
		}
		return list;
	}
}

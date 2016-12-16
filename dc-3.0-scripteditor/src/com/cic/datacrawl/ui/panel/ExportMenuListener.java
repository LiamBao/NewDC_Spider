package com.cic.datacrawl.ui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.ui.SwingGui;
import com.cic.datacrawl.ui.export.AbstractExport;
import com.cic.datacrawl.ui.export.ExportFactory;
import com.cic.datacrawl.ui.tools.CommandConstants;

public class ExportMenuListener implements PopupMenuListener, ActionListener {
	private static final Logger LOG = Logger.getLogger(ExportMenuListener.class);
	private JTabbedPane tabPanel;
	private static final String[][] types = new String[][] { { "xml", "XML" },
			{ "xls", "Microsoft Excel 2003" } };

	public ExportMenuListener(JTabbedPane tabPanel) {
		this.tabPanel = tabPanel;
		fileChooser = new JFileChooser();

		for (int i = 0; i < types.length; ++i) {
			javax.swing.filechooser.FileFilter filter = new FileChooseFilter(types[i][0], types[i][1]);
			fileChooser.addChoosableFileFilter(filter);
		}
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String filename = getSaveFilePath();
		if (!StringUtil.isEmpty(filename)) {
			String cmd = e.getActionCommand();
			String fileExtendName = filename.substring(filename.lastIndexOf(".") + 1);
			try {
				AbstractExport exportExecuter = ExportFactory.buildExport(fileExtendName, tabPanel);
				if (CommandConstants.EXPORT.getCmd().equals(cmd)) {
					exportExecuter.doExport(filename);
				} else if (CommandConstants.EXPORT_ALL.getCmd().equals(cmd)) {
					exportExecuter.doExportAll(filename);
				}
			} catch (Exception exc) {
				LOG.error(exc.getMessage());
			}
		}
	}

	private JFileChooser fileChooser = new JFileChooser();

	private String getSaveFilePath() {

		int returnVal = fileChooser.showSaveDialog(SwingGui.getInstance());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String filename = null;
			try {
				filename = fileChooser.getSelectedFile().getCanonicalPath().trim();
			} catch (IOException e) {
				return null;
			}
			if (StringUtil.isEmpty(filename)) {
				return null;
			}
			FileFilter fileFilter = fileChooser.getFileFilter();
			if (fileFilter instanceof FileChooseFilter) {
				String extendName = ("." + ((FileChooseFilter) fileFilter).getExtendName()).toLowerCase();
				if (!filename.toLowerCase().endsWith(extendName)) {
					filename = filename + extendName;
				}
				return filename;
			} else {
				for (int i = 0; i < types.length; ++i) {
					String type = "." + types[i][0].toLowerCase();
					if (filename.toLowerCase().endsWith(type)) {
						return filename;
					}
				}
			}

		}
		return null;
	}
}

class FileChooseFilter extends FileFilter {
	private String extendName;
	private String fileDesc;

	/**
	 * @return the extendName
	 */
	public String getExtendName() {
		return extendName;
	}

	public FileChooseFilter(String fileExtendName, String fileDescription) {
		extendName = fileExtendName;
		fileDesc = fileDescription;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String n = f.getName();
		int i = n.lastIndexOf('.');
		if (i > 0 && i < n.length() - 1) {
			String ext = n.substring(i + 1).toLowerCase();
			if (ext.equals(extendName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return fileDesc + " File (*." + extendName.toLowerCase() + ")";
	}
};
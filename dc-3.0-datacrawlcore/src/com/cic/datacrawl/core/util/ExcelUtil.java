package com.cic.datacrawl.core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;

public class ExcelUtil {

	private WritableWorkbook writableWorkbook;

	public static Map<String, List<BaseEntity>> readExcel(String filename) throws IOException, BiffException {
		ExcelUtil util = openExcel(filename);
		HashMap<String, List<BaseEntity>> map = new HashMap<String, List<BaseEntity>>();
		String[] sheetNames = util.getSheetNames();
		for (int i = 0; i < sheetNames.length; ++i) {
			map.put(sheetNames[i], util.readSheet(sheetNames[i]));
		}
		return map;
	}

	public List<BaseEntity> readSheet(String sheetName) {
		WritableSheet sheet = writableWorkbook.getSheet(sheetName);
		ArrayList<BaseEntity> list = new ArrayList<BaseEntity>();

		int rowCount = sheet.getRows();
		int columnCount = sheet.getColumns();
		if (rowCount > 1) {
			ArrayList<String> head = new ArrayList<String>();

			for (int j = 1; j <= columnCount; ++j) {
				head.add(j, sheet.getCell(j, 1).getContents());
			}
			for (int i = 2; i <= rowCount; ++i) {
				BaseEntity entity = new DefaultEntity(sheetName);
				for (int j = 1; j <= columnCount; ++j) {
					entity.set(head.get(j), sheet.getCell(j, 1).getContents());
				}
				list.add(entity);
			}
		}
		return list;
	}

	private static ExcelUtil openExcel(String filename) throws IOException, BiffException {
		ExcelUtil ret = new ExcelUtil();
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(Locale.CHINA);
		File f = new File(filename);
		boolean writeable = true;
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		} else {
			writeable = f.getParentFile().isDirectory();
		}
		if (!writeable) {
			throw new IOException("Can not write file in invalid path:" + f.getParent());
		}
		if (f.exists()) {
			ret.writableWorkbook = Workbook.createWorkbook(f, Workbook.getWorkbook(f));

		} else {
			ret.writableWorkbook = Workbook.createWorkbook(f, ws);
		}

		return ret;
	}

	public String[] getSheetNames() {
		return writableWorkbook.getSheetNames();
	}

	public static ExcelUtil createNewExcel(String filename, boolean overwrite) throws IOException,
			BiffException {
		ExcelUtil ret = new ExcelUtil();
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(Locale.CHINA);
		File f = new File(filename);
		boolean writeable = true;
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		} else {
			writeable = f.getParentFile().isDirectory();
		}
		if (!writeable) {
			throw new IOException("Can not write file in invalid path:" + f.getParent());
		}
		if (f.exists()) {
			boolean isDeleted = true;
			if (overwrite) {
				isDeleted = f.delete();
				if (!isDeleted)
					throw new IOException("File is opened by another application. Filename: " + filename);
				else
					ret.writableWorkbook = Workbook.createWorkbook(f, ws);
			} else {
				ret.writableWorkbook = Workbook.createWorkbook(f, Workbook.getWorkbook(f));
			}
		} else {
			ret.writableWorkbook = Workbook.createWorkbook(f, ws);
		}

		return ret;
	}

	public void addValue(String sheetname, Object values) {
		if (values instanceof Map) {
			addValue(sheetname, (Map) values);
		} else if (values instanceof List) {
			addValue(sheetname, (List) values);
		} else {
			addLineValue(getSheet(sheetname), 0, values);
		}

	}

	private WritableSheet getSheet(String sheetname) {
		WritableSheet sheet = writableWorkbook.getSheet(sheetname);
		if (sheet == null) {
			sheet = writableWorkbook.createSheet(sheetname, writableWorkbook.getNumberOfSheets() + 1);
		}
		return sheet;
	}

	public void addValue(String sheetname, Map values) {
		WritableSheet sheet = getSheet(sheetname);
		Object[] keys = new Object[values.size()];
		values.keySet().toArray(keys);
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; ++i) {
			Object obj = values.get(keys[i]);
			addLineValue(sheet, i, obj);
		}
	}

	public void addValue(String sheetname, List values) {
		WritableSheet sheet = getSheet(sheetname);
		for (int i = 0; i < values.size(); ++i) {
			if (i == 0) {
				if (values.get(i) instanceof BaseEntity) {
					addTitle(sheet, (BaseEntity) values.get(i));
				}
			}
			addLineValue(sheet, i, values.get(i));
		}
	}

	private void addLineValue(WritableSheet sheet, int row, Object lineValues) {
		if (lineValues instanceof Map) {
			addValue(sheet, row, (Map) lineValues);
		} else if (lineValues instanceof List) {
			addValue(sheet, row, (List) lineValues);
		} else if (lineValues instanceof BaseEntity) {
			addLineValue(sheet, row + 1, (BaseEntity) lineValues);
		} else {
			addValue(sheet, row, 0, lineValues);
		}
	}

	private void addTitle(WritableSheet sheet, BaseEntity entity) {
		if (entity == null || entity.getValueMap().size() == 0)
			return;

		String[] columns = entity.getColumnNames();
		for (int i = 0; i < columns.length; ++i) {
			addValue(sheet, 0, i, columns[i]);
		}
	}

	private void addLineValue(WritableSheet sheet, int row, BaseEntity entity) {
		if (entity == null || entity.getValueMap().size() == 0)
			return;

		String[] columns = entity.getColumnNames();
		for (int i = 0; i < columns.length; ++i) {
			addValue(sheet, row, i, entity.getString(columns[i]));
		}
		// if (lineValues instanceof Map) {
		// addValue(sheet, row, (Map) lineValues);
		// } else if (lineValues instanceof List) {
		// addValue(sheet, row, (List) lineValues);
		// } else if (lineValues instanceof BaseEntity) {
		// addValue(sheet, row, (BaseEntity) lineValues);
		// } else {
		// addValue(sheet, row, 0, lineValues);
		// }
	}

	private void addValue(WritableSheet sheet, int row, int column, Object obj) {
		Label cell = new Label(column, row, ObjectUtils.toString(obj));
		try {
			sheet.addCell(cell);
		} catch (RowsExceededException e) {
			throw new RuntimeException(e);
		} catch (WriteException e) {
			throw new RuntimeException(e);
		}
	}

	private void addValue(WritableSheet sheet, int row, Map lineValues) {
		Object[] keys = new Object[lineValues.size()];
		lineValues.keySet().toArray(keys);
		Arrays.sort(keys);
		for (int j = 0; j < keys.length; ++j) {
			Object obj = lineValues.get(keys[j]);
			addValue(sheet, row, j, obj);
		}
	}

	private void addValue(WritableSheet sheet, int row, List lineValues) {
		for (int j = 0; j < lineValues.size(); ++j) {
			addValue(sheet, row, j, lineValues.get(j));
		}
	}

	public void save() throws WriteException, IOException {
		if (writableWorkbook != null) {
			try {
				writableWorkbook.write();
			} catch (IOException e) {
				throw e;
			} finally {
				close();
			}
		}
	}

	public void copySheet(String sheetname, String newSheetname) {
		int newSheetIndex = writableWorkbook.getNumberOfSheets() + 1;
		writableWorkbook.copySheet(sheetname, newSheetname, newSheetIndex);
	}

	public void close() throws WriteException, IOException {
		if (writableWorkbook != null) {
			writableWorkbook.close();
			writableWorkbook = null;
		}
	}
}

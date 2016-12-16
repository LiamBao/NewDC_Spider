package com.cic.datacrawl.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Kit;

public class FileUtils {
	private static final Logger LOG = Logger.getLogger(FileUtils.class);

	public static File[] listAllFiles(File folder, String typename, final boolean isRecursion) {
		if (folder == null || folder.isFile()) {
			return null;
		}

		final String type = typename.toLowerCase();

		ArrayList<File> fileList = new ArrayList<File>();

		File[] files = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return ((isRecursion && file.isDirectory()) || (file.isFile() && file.getAbsolutePath()
						.toLowerCase().endsWith(type)));
			}
		});

		for (int i = 0; i < files.length; ++i) {
			if (files[i].isDirectory()) {
				File[] subFiles = listAllFiles(files[i], typename, isRecursion);
				if (subFiles != null) {
					for (int j = 0; j < subFiles.length; ++j) {
						fileList.add(subFiles[j]);
					}
				}
			} else {
				fileList.add(files[i]);
			}
		}

		File[] ret = new File[fileList.size()];
		fileList.toArray(ret);
		return ret;
	}
	
	public static File makeEmptyDir(String pathName){
		File dir = new File(pathName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static File makeDirs(String pathName) {
		int index = 1;
		boolean makeDirs = false;
		File outputFile = null;
		while (!makeDirs) {
			outputFile = new File(pathName);
			if (outputFile.exists()) {
				makeDirs = outputFile.isDirectory();
			} else {
				makeDirs = outputFile.mkdirs();
			}
			if (!makeDirs) {
				pathName = pathName + "_" + index;
			}
			index++;
		}
		return outputFile;
	}

	private static String getIPAddress() {
		String hostIP = "127.0.0.1";

		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();

			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				Enumeration<?> e2 = ni.getInetAddresses();
				boolean found = false;
				while (e2.hasMoreElements()) {
					InetAddress ia = (InetAddress) e2.nextElement();
					if (ia instanceof Inet6Address)
						continue; // omit IPv6 address
					hostIP = ia.getHostAddress();
					// System.out.println("addr:" + hostIP);
					if (!hostIP.equals("127.0.0.1")) {
						found = true;
						break;
					}
				}
				if (found)
					break;
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return hostIP.replace(".", "_");
	}

	public static String createZipFileName() {
		Date date = new Date();
		String address = getIPAddress();

		return address + "-" + DateUtil.format(date, "yyyy_MM_dd-HH_mm_ss") + ".zip";

	}

	public static String getParentAbsolutePath(File file) {
		if (file == null || !file.exists())
			return null;
		File parentFolder = file.getParentFile();
		if (parentFolder == null || !parentFolder.exists())
			return null;
		return parentFolder.getAbsolutePath();
	}

	public static String getParentAbsolutePath(String fileUrl) {
		if (fileUrl == null || fileUrl.trim().length() == 0)
			return null;
		File f = new File(fileUrl);
		return getParentAbsolutePath(f);
	}

	public static String buildAbsolutelyPath(String path, String filename) {
		if (filename.startsWith(path))
			return filename;
		if (path.endsWith(File.separator) && filename.startsWith(File.separator)) {
			return path + filename.substring(1);
		} else if (!path.endsWith(File.separator) && !filename.startsWith(File.separator)) {
			return path + File.separator + filename;
		} else {
			return path + filename;
		}
	}

	/**
	 * Reads the file with the given name and returns its contents as a String.
	 * 
	 * @throws IOException
	 */
	public static String readFile(InputStream input, String encoding) throws IOException {
		String text;
		try {
			Reader r = new BufferedReader(new InputStreamReader(input, encoding));

			text = Kit.readReader(r);
		} catch (IOException ex) {
			text = null;
			throw ex;
		}
		return text;
	}

	/**
	 * Reads the file with the given name and returns its contents as a String.
	 * 
	 * @throws IOException
	 */
	public static String readFile(File file, String encoding) throws IOException {
		InputStream input = null;
		input = new FileInputStream(file);
		try {
			return readFile(input, encoding);
		} catch (IOException e) {
			throw e;
		} finally {
			if (input != null)
				input.close();
		}
	}

	/**
	 * Reads the file with the given name and returns its contents as a String.
	 * 
	 * @throws IOException
	 */
	public static String readFile(String filename, String encoding) throws IOException {
		InputStream input = null;
		input = new FileInputStream(filename);
		try {
			return readFile(input, encoding);
		} catch (IOException e) {
			throw e;
		} finally {
			if (input != null)
				input.close();
		}
	}

	/**
	 * Reads the file with the given name and returns its contents as a String.
	 * 
	 * @throws IOException
	 */
	public static String readFile(InputStream input) throws IOException {
		return readFile(input, "UTF-8");
	}

	/**
	 * Reads the file with the given name and returns its contents as a String.
	 * 
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		return readFile(file, "UTF-8");
	}

	/**
	 * Reads the file with the given name and returns its contents as a String.
	 * 
	 * @throws IOException
	 */
	public static String readFile(String filename) throws IOException {
		return readFile(filename, "UTF-8");
	}

	public static boolean move(File src, File destFolder, boolean overwrite) throws IOException {
		if (src == null || !src.exists() || destFolder == null || destFolder.isFile()) {
			return false;
		}
		if (!destFolder.exists()) {
			boolean createDestFolder = destFolder.mkdirs();
			if (!createDestFolder) {
				if (LOG.isDebugEnabled())
					LOG.debug("Can not create destination folder(" + destFolder.getAbsolutePath() + ").");

				return false;
			}
		}
		boolean ret = true;
		if (src.isDirectory()) {
			File[] childFiles = src.listFiles();
			File destSubFolder = new File(destFolder.getAbsolutePath() + File.separator + src.getName());
			boolean canMove = false;
			if (destSubFolder.exists()) {
				if (LOG.isDebugEnabled())
					LOG.debug("Folder(" + destFolder.getAbsolutePath() + ") is exists.");

				canMove = destSubFolder.isDirectory();
			} else {
				if (LOG.isDebugEnabled())
					LOG.debug("Make dir " + destFolder.getAbsolutePath() + ".");

				canMove = destSubFolder.mkdirs();
			}
			if (canMove) {
				for (int i = 0; i < childFiles.length && ret; ++i) {
					ret = ret & move(childFiles[i], destSubFolder, overwrite);
				}
				return ret & src.delete();
			} else {
				if (LOG.isDebugEnabled())
					LOG.debug("Folder(" + destFolder.getAbsolutePath() + ") can not be moved.");
			}
		} else {
			ret = moveFile(src, destFolder, overwrite);
		}

		return true;
	}

	private static boolean moveFile(File src, File destFolder, boolean overwrite) throws IOException {
		if (src == null || src.isDirectory() || !src.exists() || destFolder == null || destFolder.isFile()) {
			return false;
		}
		if (!destFolder.exists()) {
			boolean createDestFolder = destFolder.mkdirs();
			if (!createDestFolder) {
				if (LOG.isDebugEnabled())
					LOG.debug("Can not create destination folder(" + destFolder.getAbsolutePath() + ").");
				return false;
			}
		}

		File destFile = new File(destFolder + File.separator + src.getName());
		if (destFile.exists()) {
			if (!overwrite)
				return true;
		} else {
			destFile.createNewFile();
		}

		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		long size = 0;
		try {
			input = new BufferedInputStream(new FileInputStream(src));
			output = new BufferedOutputStream(new FileOutputStream(destFile));
			size = IOUtils.copyLarge(input, output);
		} catch (IOException e) {
			throw e;
		} finally {
			if (input != null)
				input.close();
			if (output != null)
				output.close();
		}
		if (size == src.length()) {
			boolean isDeleted = src.delete();
			if (LOG.isDebugEnabled()) {
				if (isDeleted) {
					LOG.debug("Deleted " + src.getAbsolutePath() + ".");
				} else {
					LOG.debug("Can not delete " + src.getAbsolutePath() + ".");
				}
			}
			return isDeleted;
		} else {

		}

		return false;
	}

	public static String buildValidFileName(String fileName, boolean overwrite) throws IOException {
		return createFile(fileName, overwrite).getAbsolutePath();
	}

	public static File createFile(String fileName, boolean overwrite) throws IOException {
		String retFilename = fileName;

		File f = new File(fileName);
		boolean writeable = true;
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		} else {
			writeable = f.getParentFile().isDirectory();
		}
		if (!writeable) {
			throw new IOException("Can not write file in invalid path:" + f.getParent());
		}

		int splitIndex = fileName.lastIndexOf(".");
		String filename = splitIndex > 0 ? fileName.substring(0, splitIndex) : fileName;
		String fileType = splitIndex > 0 ? fileName.substring(splitIndex) : "";

		int index = 0;
		while (f.exists()) {
			if (overwrite) {
				if (!f.delete()) {
					throw new IOException("Can not overwrite file: " + fileName);
				}
			} else {
				++index;
				retFilename = filename + "_" + index + fileType;
				f = new File(retFilename);
			}
		}
		return f;
	}

	/**
	 * write text into a file.
	 * 
	 * @throws IOException
	 */
	public static File saveFile(String fileName, String text, boolean overwrite) throws IOException {

		File f = new File(buildValidFileName(fileName, overwrite));
		if (!f.exists())
			f.createNewFile();

		try {
			BufferedReader r = new BufferedReader(new StringReader(text));
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			try {
				String line = null;
				int lineNum = 1;
				while ((line = r.readLine()) != null) {
					if (lineNum > 1) {
						w.write("\n");
					}
					w.write(StringUtil.fromDefaultToUTF8(line));
					++lineNum;
				}
			} finally {
				w.close();
				r.close();
			}
		} catch (IOException ex) {
			throw ex;
		}
		return f;
	}

	/**
	 * write binary content into a file.
	 * 
	 * @throws IOException
	 */
	public static String saveFile(String fileName, byte[] contents, boolean overwrite) throws IOException {
		File f = new File(buildValidFileName(fileName, overwrite));
		f.createNewFile();

		try {
			BufferedOutputStream w = new BufferedOutputStream(new FileOutputStream(f));
			if (contents != null && contents.length > 0) {
				try {

					for (int i = 0; i < contents.length; ++i) {
						w.write(contents[i]);
					}
				} finally {
					w.flush();
					w.close();
				}
			}
		} catch (IOException ex) {
			throw ex;
		}
		return f.getAbsolutePath();
	}

	public static byte[] toZipByteArray(byte[] content) throws IOException {
		if (content == null || content.length == 0)
			return new byte[0];

		byte[] bytes = null;
		ZipOutputStream zipOutput = null;
		ByteArrayOutputStream output = null;
		output = new ByteArrayOutputStream();
		zipOutput = new ZipOutputStream(output);
		zipOutput.setLevel(9);
		ZipEntry zipEntry = new ZipEntry("content");
		zipEntry.setExtra(content);
		try {
			zipOutput.putNextEntry(zipEntry);

			// while(input.read(bytes) != -1){
			// zipOutput.write(bytes);
			// }

			zipOutput.finish();
			bytes = output.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			zipOutput.close();
		}
		return bytes;
	}

	public static byte[] readZipStream(ZipInputStream input) throws IOException {
		if (input == null)
			return new byte[0];

		byte[] bytes = null;
		try {
			bytes = readZipStream(input.getNextEntry());
		} catch (IOException e) {
			throw e;
		}

		return bytes;

	}

	public static byte[] readZipStream(byte[] zipContent) throws IOException {
		if (zipContent == null || zipContent.length == 0)
			return new byte[0];

		byte[] bytes = null;
		ByteArrayInputStream input = null;
		ZipInputStream zipInput = null;
		input = new ByteArrayInputStream(zipContent);
		zipInput = new ZipInputStream(input);
		try {
			bytes = readZipStream(zipInput);
		} catch (IOException e) {
			throw e;
		} finally {
			if (zipInput != null)
				zipInput.close();
		}
		return bytes;
	}

	public static byte[] readZipStream(ZipEntry entry) {
		if (entry == null)
			return new byte[0];

		return entry.getExtra();
	}

	public static void main(String[] args) {
		String zipFile = FileUtils.createZipFileName();
		System.out.println(zipFile);
	}

	/**
	 * 根据文件路径删除文件
	 * @param path
	 * @return
	 */
	public static boolean doDeleteFile(String path){
		return new File(path).delete();
	}
	
	/**
	 * 将文件按照修改时间排序
	 * @param fs
	 * @param desc true :降序  	false:升序
	 */
	public static void orderByDate(File[] fs, final boolean desc) {
		
		Arrays.sort(fs,new Comparator<File>(){
			public int compare(File f1, File f2) {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
				{
					if(desc){
						return -1;
					}else{
						return 1;
					}
				}
				else if (diff == 0)
					return 0;
				else{
					if(desc){
						return 1;
					}else{
						return -1;
					}
				}
			}
			public boolean equals(Object obj) {
				return true;
			}
		});
		
	}
	
	/**
	 * 将文件大小排序
	 * @param fs
	 * @param desc true :降序  	false:升序
	 */
	public static void orderByLength(File[] fs, final boolean desc) {
		List<File> files = Arrays.asList(fs);
		Collections.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				long diff = f1.length() - f2.length();
				if (diff > 0)
					if(desc){
						return -1;
					}else{
						return 1;
					}
				else if (diff == 0)
					return 0;
				else
					if(desc){
						return 1;
					}else{
						return -1;
					}
			}
			public boolean equals(Object obj) {
				return true;
			}
		});
		
	}

	/**
	 * 将文件名称排序
	 * @param fs
	 * @param desc true :降序  	false:升序
	 */
	public static void orderByName(File[] fs, final boolean desc) {
		List<File> files = Arrays.asList(fs);
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile())
				{
					if(desc){
						return 1;
					}else{
						return -1;
					}
				}
				if (o1.isFile() && o2.isDirectory())
				{
					if(desc){
						return -1;
					}else{
						return 1;
					}
				}
				
				if(desc){
					return o2.getName().compareTo(o1.getName());
				}else{
					return o1.getName().compareTo(o2.getName());
				}
				
			}
		});
	}
}

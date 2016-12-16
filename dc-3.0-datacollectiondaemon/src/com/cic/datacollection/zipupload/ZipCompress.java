package com.cic.datacollection.zipupload;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.selectors.FileSelector;

public class ZipCompress {
	
	private File zipFile;
	public ZipCompress(String pathName){
		zipFile = new File(pathName);
	}
	
	public ArrayList<String> compress(String srcPathName){
		File srcdir = new File(srcPathName);
		final ArrayList<String> filenames = new ArrayList<String>();
		
		
		if(!srcdir.exists()){
			throw new RuntimeException(srcPathName + "不存在");
		}
		
		
		
		Project prj = new Project();
		Zip zip = new Zip();
		zip.setProject(prj);
		zip.setDestFile(zipFile);
		
		zip.setBasedir(srcdir);
		
		zip.add(new FileSelector() {
			
			@Override
			public boolean isSelected(File basedir, String filename, File file) throws BuildException {
				
				if(filename.toLowerCase().endsWith(".xml") && file.length() > 0){
					filenames.add(filename);
					return true;
				}
				return false;
			}
		});
		
		try{
			zip.execute();
		}catch(BuildException e){
			throw new RuntimeException(e.getMessage());
		}
		
		return filenames;
	}

}

package com.cic.datacollection.zipupload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHExec;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FilenameSelector;

import com.cic.datacrawl.core.rpc.ClientImpl;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.util.FileUtils;

/*
 * 该类是用来将数据打包成Zip文件，并上传到DataCenter中
 */
public class DataToCenter {
	
	private static final Logger LOG = Logger.getLogger(DataToCenter.class);

	public static void main(String[] args){
		
		try {
			

		String path = null;
		String xmlPath = null;
		boolean reflash = true;
		
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("-h")) {
				System.out.println("Command Format: \n"
									+ "\t%JAVA_HOME%\\BIN\\JAVA -jar dts.jar "
									+ "[-d config path]\n"
									+ "\t%JAVA_HOME%\\BIN\\JAVA -jar dts.jar "
									+ "[-d config path_1;path_2;....;path_n]\n");
			}

			int pathIndex = ArrayUtils.indexOf(args, "-d");
			if (pathIndex >= 0) {
				try {
					path = args[pathIndex + 1];
					File f = new File(path);
					if (!f.exists()) {
						LOG.warn("Invalid path of configuration. " + "Using default configuration.");
					}
				} catch (Throwable e) {
					LOG.warn("Invalid path of configuration. " + "Using default configuration.");
				}
			}
			
			

			// int reflashIndex = ArrayUtils.indexOf(args, "-r");
			//
			// reflash = new Boolean(args[reflashIndex + 1]).booleanValue();
		}
		if (path == null || path.trim().length() == 0)
			path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";

		LOG.debug("Config Path: \"" + path + "\"");
		// 启动IOC容器
		// 启动配置管理程序
		// 装载默认配置文件
		ApplicationContext.initialiaze(path, reflash);

		xmlPath = Config.getInstance().getOutputPath();
		
		/*
		 * 加载conf.xml文件读取DataCenter的信息，用户名，密码，零时路径，处理路径，端口号，地址
		 */
		
		Configuration conf = new Configuration();
		conf.addResource(new Path("conf/conf.xml"));
		
		String dataCenterAddress = conf.get("data.center.address", "localhost");
		int dataCenterPort = conf.getInt("data.center.port", 16001);
		String dataCenterUser = conf.get("data.center.user", "steven");
		String dataCenterPasswd = conf.get("data.center.passwd", "cicdata");
		String dataCenterTempSavePath = conf.get("data.center.temp.savepath", "/home/dc_opr/SaveData_temp");
		
		
		/*
		 * 接下来的工作就是将数据上传到DataCenter中
		 */
		//xmlPath = "/home/steven/workspace/DataCollectionDaemon/testFile";
		File xmlFolder = new File(xmlPath);
		if(!xmlFolder.exists() || xmlFolder.isFile()){
			return;
		}
		
		//压缩文件
		//生成文件名
		String zipFileName = FileUtils.createZipFileName();
		ZipCompress compress = new ZipCompress(zipFileName);
		ArrayList<String> filenames = compress.compress(xmlPath);
		
		//生成零时文件夹，将压缩的文件存放到temp文件夹中
		
		File tempZipFolder = new File(xmlFolder.getParentFile().getAbsolutePath()
										+ File.separator
										+ "zip_temp");
		if(!tempZipFolder.exists()){
			tempZipFolder.mkdirs();
		}
		try {
			FileUtils.move(new File(zipFileName), tempZipFolder , true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		File zipXmlFolder = new File(xmlFolder.getParentFile().getAbsolutePath()
				+ File.separator
				+ "zip"
				+ xmlFolder.getName());

		if (!zipXmlFolder.exists()) {
			zipXmlFolder.mkdirs();
		}
		
		//将压缩的文件移除到old文件夹下
		File oldXmlFolder = new File(xmlFolder.getParentFile().getAbsolutePath()
										+ File.separator
										+ "old"
										+ xmlFolder.getName());

		if (!oldXmlFolder.exists()) {
			oldXmlFolder.mkdirs();
		}
		
		String xmlFolderPath = xmlFolder.getAbsolutePath();
		
		for (int i = 0; i < filenames.size(); ++i) {
			
			File xmlFile = new File(xmlFolderPath+File.separator+filenames.get(i));
			LOG.info("DTS File: " + xmlFile.getAbsolutePath());
			

			try {
				FileUtils.move(xmlFile, oldXmlFolder, true);
			} catch (Exception e) {
				LOG.error("There is an exception on dataToCenter File: " + xmlFile.getAbsolutePath(), e);
			}
		}
		
		//将temp文件夹中的zip文件上传到DataCenter中
		
		
		File[] zipFiles = tempZipFolder.listFiles();
		
		System.out.println("zip count: "+zipFiles.length);
		
		for(int i =0;i<zipFiles.length;i++){
			
			File zipFile = zipFiles[i];
			
			
			//第一步上传到temp文件夹中
			try{
				scpTempData(tempZipFolder.getAbsolutePath(),zipFile.getName(), dataCenterAddress, dataCenterUser, dataCenterPasswd, dataCenterTempSavePath);
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
			//第二步，通知datacenter将上传成功的zip从temp中移除到savedata文件夹中
			try{
				ClientImpl client = new ClientImpl(dataCenterAddress, dataCenterPort);
				Text fileNameWritable = new Text(zipFile.getName());
				FeedBackWritable object =(FeedBackWritable) client.execute_proxy("finishDataToCenter", fileNameWritable);
				if (object.code!=CodeStatus.succCode){
					System.out.println(object.errorMessage);
					continue;
				}else{
					//第三步，将本地temp下的zip文件删除掉
					//zipFile.delete();
					try {
						FileUtils.move(zipFile, zipXmlFolder, true);
					} catch (Exception e) {
						LOG.error("There is an exception on dataToCenter File: " + zipFile.getAbsolutePath(), e);
					}
					
				}
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
			
			
		}
		} catch (Throwable e) {
			e.printStackTrace();
			LOG.error("DataToCenter is error", e);
		}
	}
	
	public static void scpTempData(String filefolder, String filename, String address, String user, String passwd, String remoteDir) throws IOException,
		InterruptedException {

		String host = address;

		String localPath = filename;
		
		remoteDir = remoteDir.replace(" ", "\\ ");
		String remotePath = String.format("%s@%s:%s/", user, host, remoteDir);

		System.out.println(remotePath);

		SSHExec ssh = new SSHExec();
		ssh.setTrust(true);
		ssh.setUsername(user);
		ssh.setHost(host);
		ssh.setPassword(passwd);
		ssh.setCommand(String.format("mkdir -p %s", remoteDir));
		ssh.execute();

		Scp scp = new Scp();
		scp.setTrust(true);
		scp.setHost(host);
		scp.setUsername(user);
		scp.setPassword(passwd);
		scp.setRemoteTodir(remotePath);

		FilenameSelector selector = new FilenameSelector();
		selector.setName(localPath);
		FileSet fileset = new FileSet();
		fileset.setDir(new File(filefolder));
		//fileset.setFile(new File(localPath));
		fileset.addFilename(selector);
		scp.addFileset(fileset);

		Project project = new Project();
		project.init();
		scp.setProject(project);
		scp.execute();
	}
	

}

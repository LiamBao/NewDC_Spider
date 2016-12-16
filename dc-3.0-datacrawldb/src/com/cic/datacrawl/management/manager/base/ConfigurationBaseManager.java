//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.Configuration;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.ConfigurationDAO;

public abstract class ConfigurationBaseManager{

	protected ConfigurationDAO dao;
	protected ConfigurationDAO getConfigurationDAO() {		
		if(dao == null){
			dao = (ConfigurationDAO) ApplicationContext.getInstance().getBean("configurationDAO");
		}
		return dao;
	}
	
	public void setConfigurationDAO(ConfigurationDAO dao) {
		this.dao = dao;
	}
	
	public Configuration[] getAllConfiguration() {
		return getConfigurationDAO().getAllConfiguration();
	}
	
	public Configuration[] getAllConfiguration(int startIndex, int limit) {
		return getConfigurationDAO().getAllConfiguration(startIndex, limit);
	}
	
	public Configuration getConfiguration(long id){
		return getConfigurationDAO().getConfiguration(id);
	}

	public Configuration[] getConfigurations(long[] ids){
		return getConfigurationDAO().getConfigurations(ids);
	}
	
	public long addConfiguration(Configuration configuration){
		return getConfigurationDAO().addConfiguration(configuration);
	}	
	
	public long[] addConfigurations(Configuration[] configurations){
		return getConfigurationDAO().addConfigurations(configurations);
	}
	
	public int deleteConfiguration(long id){
		return getConfigurationDAO().deleteConfiguration(id);
	}
	
	public int deleteConfigurations(long[] ids){
		return getConfigurationDAO().deleteConfigurations(ids);
	}
	
	public long saveConfiguration(Configuration configuration){
		long ret = getConfigurationDAO().saveConfiguration(configuration);
		if (ret == 0) {
			ret = getConfigurationDAO().addConfiguration(configuration);
		} else {
			ret = configuration.getId();
		}
		return ret;
	}
	
	public int updateConfiguration(Configuration configuration){
		return getConfigurationDAO().saveConfiguration(configuration);		
	}
	
	public int[] updateConfigurations(Configuration[] configurations){
		return getConfigurationDAO().saveConfigurations(configurations);		
	}
	
	public int count(){
		return getConfigurationDAO().count();
	}
	
}

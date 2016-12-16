//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.Configuration;

public interface ConfigurationBaseDAO {

	public Configuration[] getAllConfiguration();

	public Configuration[] getAllConfiguration(int startIndex, int limit);

	public Configuration getConfiguration(long id);

	public Configuration[] getConfigurations(long[] id);
	
	public long addConfiguration(Configuration configuration);
	
	public long[] addConfigurations(Configuration[] configurations);
	
	public int deleteConfiguration(long id);
	
	public int deleteConfigurations(long[] id);
	
	public int saveConfiguration(Configuration configuration);
	
	public int[] saveConfigurations(final Configuration[] configurations);
	
	public int count();
	
}

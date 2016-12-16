//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.Group;

public interface GroupBaseDAO {

	public Group[] getAllGroup();

	public Group[] getAllGroup(int startIndex, int limit);

	public Group getGroup(long id);

	public Group[] getGroups(long[] id);
	
	public long addGroup(Group group);
	
	public long[] addGroups(Group[] groups);
	
	public int deleteGroup(long id);
	
	public int deleteGroups(long[] id);
	
	public int saveGroup(Group group);
	
	public int[] saveGroups(final Group[] groups);
	
	public int count();
	
	public Group queryByName(final java.lang.String name);
}

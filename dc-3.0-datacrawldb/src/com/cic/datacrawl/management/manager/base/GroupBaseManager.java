//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.Group;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.GroupDAO;

public abstract class GroupBaseManager{

	protected GroupDAO dao;
	protected GroupDAO getGroupDAO() {		
		if(dao == null){
			dao = (GroupDAO) ApplicationContext.getInstance().getBean("groupDAO");
		}
		return dao;
	}
	
	public void setGroupDAO(GroupDAO dao) {
		this.dao = dao;
	}
	
	public Group[] getAllGroup() {
		return getGroupDAO().getAllGroup();
	}
	
	public Group[] getAllGroup(int startIndex, int limit) {
		return getGroupDAO().getAllGroup(startIndex, limit);
	}
	
	public Group getGroup(long id){
		return getGroupDAO().getGroup(id);
	}

	public Group[] getGroups(long[] ids){
		return getGroupDAO().getGroups(ids);
	}
	
	public long addGroup(Group group){
		return getGroupDAO().addGroup(group);
	}	
	
	public long[] addGroups(Group[] groups){
		return getGroupDAO().addGroups(groups);
	}
	
	public int deleteGroup(long id){
		return getGroupDAO().deleteGroup(id);
	}
	
	public int deleteGroups(long[] ids){
		return getGroupDAO().deleteGroups(ids);
	}
	
	public long saveGroup(Group group){
		long ret = getGroupDAO().saveGroup(group);
		if (ret == 0) {
			ret = getGroupDAO().addGroup(group);
		} else {
			ret = group.getId();
		}
		return ret;
	}
	
	public int updateGroup(Group group){
		return getGroupDAO().saveGroup(group);		
	}
	
	public int[] updateGroups(Group[] groups){
		return getGroupDAO().saveGroups(groups);		
	}
	
	public int count(){
		return getGroupDAO().count();
	}
	
	public Group queryByName(final java.lang.String name ){
		return getGroupDAO().queryByName(name);
	}
}

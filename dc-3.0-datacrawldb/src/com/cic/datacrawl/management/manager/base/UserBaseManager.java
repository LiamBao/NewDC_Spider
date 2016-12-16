//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.User;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.UserDAO;

public abstract class UserBaseManager{

	protected UserDAO dao;
	protected UserDAO getUserDAO() {		
		if(dao == null){
			dao = (UserDAO) ApplicationContext.getInstance().getBean("userDAO");
		}
		return dao;
	}
	
	public void setUserDAO(UserDAO dao) {
		this.dao = dao;
	}
	
	public User[] getAllUser() {
		return getUserDAO().getAllUser();
	}
	
	public User[] getAllUser(int startIndex, int limit) {
		return getUserDAO().getAllUser(startIndex, limit);
	}
	
	public User getUser(long id){
		return getUserDAO().getUser(id);
	}

	public User[] getUsers(long[] ids){
		return getUserDAO().getUsers(ids);
	}
	
	public long addUser(User user){
		return getUserDAO().addUser(user);
	}	
	
	public long[] addUsers(User[] users){
		return getUserDAO().addUsers(users);
	}
	
	public int deleteUser(long id){
		return getUserDAO().deleteUser(id);
	}
	
	public int deleteUsers(long[] ids){
		return getUserDAO().deleteUsers(ids);
	}
	
	public long saveUser(User user){
		long ret = getUserDAO().saveUser(user);
		if (ret == 0) {
			ret = getUserDAO().addUser(user);
		} else {
			ret = user.getId();
		}
		return ret;
	}
	
	public int updateUser(User user){
		return getUserDAO().saveUser(user);		
	}
	
	public int[] updateUsers(User[] users){
		return getUserDAO().saveUsers(users);		
	}
	
	public int count(){
		return getUserDAO().count();
	}
	
}

//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.User;

public interface UserBaseDAO {

	public User[] getAllUser();

	public User[] getAllUser(int startIndex, int limit);

	public User getUser(long id);

	public User[] getUsers(long[] id);
	
	public long addUser(User user);
	
	public long[] addUsers(User[] users);
	
	public int deleteUser(long id);
	
	public int deleteUsers(long[] id);
	
	public int saveUser(User user);
	
	public int[] saveUsers(final User[] users);
	
	public int count();
	
}

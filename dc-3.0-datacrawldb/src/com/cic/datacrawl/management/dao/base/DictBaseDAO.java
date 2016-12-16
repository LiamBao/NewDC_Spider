//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.Dict;

public interface DictBaseDAO {

	public Dict[] getAllDict();

	public Dict[] getAllDict(int startIndex, int limit);

	public Dict getDict(long id);

	public Dict[] getDicts(long[] id);
	
	public long addDict(Dict dict);
	
	public long[] addDicts(Dict[] dicts);
	
	public int deleteDict(long id);
	
	public int deleteDicts(long[] id);
	
	public int saveDict(Dict dict);
	
	public int[] saveDicts(final Dict[] dicts);
	
	public int count();
	
	public Dict[] queryByType(final java.lang.String type );
	public Dict queryByTypeValue(final java.lang.String type, final java.lang.String value);
}

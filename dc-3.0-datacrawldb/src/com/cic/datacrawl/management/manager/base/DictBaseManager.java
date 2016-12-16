//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.Dict;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.DictDAO;

public abstract class DictBaseManager{

	protected DictDAO dao;
	protected DictDAO getDictDAO() {		
		if(dao == null){
			dao = (DictDAO) ApplicationContext.getInstance().getBean("dictDAO");
		}
		return dao;
	}
	
	public void setDictDAO(DictDAO dao) {
		this.dao = dao;
	}
	
	public Dict[] getAllDict() {
		return getDictDAO().getAllDict();
	}
	
	public Dict[] getAllDict(int startIndex, int limit) {
		return getDictDAO().getAllDict(startIndex, limit);
	}
	
	public Dict getDict(long id){
		return getDictDAO().getDict(id);
	}

	public Dict[] getDicts(long[] ids){
		return getDictDAO().getDicts(ids);
	}
	
	public long addDict(Dict dict){
		return getDictDAO().addDict(dict);
	}	
	
	public long[] addDicts(Dict[] dicts){
		return getDictDAO().addDicts(dicts);
	}
	
	public int deleteDict(long id){
		return getDictDAO().deleteDict(id);
	}
	
	public int deleteDicts(long[] ids){
		return getDictDAO().deleteDicts(ids);
	}
	
	public long saveDict(Dict dict){
		long ret = getDictDAO().saveDict(dict);
		if (ret == 0) {
			ret = getDictDAO().addDict(dict);
		} else {
			ret = dict.getId();
		}
		return ret;
	}
	
	public int updateDict(Dict dict){
		return getDictDAO().saveDict(dict);		
	}
	
	public int[] updateDicts(Dict[] dicts){
		return getDictDAO().saveDicts(dicts);		
	}
	
	public int count(){
		return getDictDAO().count();
	}
	
	public Dict[] queryByType(final java.lang.String type ){
		 return getDictDAO().queryByType(type );
	}
	public Dict queryByTypeValue(final java.lang.String type, final java.lang.String value ){
		return getDictDAO().queryByTypeValue(type,value);
	}
}

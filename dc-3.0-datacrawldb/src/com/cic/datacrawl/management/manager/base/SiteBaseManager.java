//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.Site;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.SiteDAO;

public abstract class SiteBaseManager{

	protected SiteDAO dao;
	protected SiteDAO getSiteDAO() {		
		if(dao == null){
			dao = (SiteDAO) ApplicationContext.getInstance().getBean("siteDAO");
		}
		return dao;
	}
	
	public void setSiteDAO(SiteDAO dao) {
		this.dao = dao;
	}
	
	public Site[] getAllSite() {
		return getSiteDAO().getAllSite();
	}
	
	public Site[] getAllSite(int startIndex, int limit) {
		return getSiteDAO().getAllSite(startIndex, limit);
	}
	
	public Site getSite(long id){
		return getSiteDAO().getSite(id);
	}

	public Site[] getSites(long[] ids){
		return getSiteDAO().getSites(ids);
	}
	
	public long addSite(Site site){
		return getSiteDAO().addSite(site);
	}	
	
	public long[] addSites(Site[] sites){
		return getSiteDAO().addSites(sites);
	}
	
	public int deleteSite(long id){
		return getSiteDAO().deleteSite(id);
	}
	
	public int deleteSites(long[] ids){
		return getSiteDAO().deleteSites(ids);
	}
	
	public long saveSite(Site site){
		long ret = getSiteDAO().saveSite(site);
		if (ret == 0) {
			ret = getSiteDAO().addSite(site);
		} else {
			ret = site.getId();
		}
		return ret;
	}
	
	public int updateSite(Site site){
		return getSiteDAO().saveSite(site);		
	}
	
	public int[] updateSites(Site[] sites){
		return getSiteDAO().saveSites(sites);		
	}
	
	public int count(){
		return getSiteDAO().count();
	}
	
	public Site[] queryByQA(final long qaId ){
		 return getSiteDAO().queryByQA(qaId );
	}
	public Site[] queryByRW(final long rwId ){
		 return getSiteDAO().queryByRW(rwId );
	}
	public Site[] queryByType(final java.lang.String type ){
		 return getSiteDAO().queryByType(type );
	}
	public Site[] queryByGroupId(final long groupId ){
		 return getSiteDAO().queryByGroupId(groupId );
	}
	public long countByQA(final long qaId ){
		return getSiteDAO().countByQA(qaId);
	}
	public long countByRW(final long rwId ){
		return getSiteDAO().countByRW(rwId);
	}
	public long countByType(final java.lang.String type ){
		return getSiteDAO().countByType(type);
	}
	public long countByGroupId(final long groupId ){
		return getSiteDAO().countByGroupId(groupId);
	}
}

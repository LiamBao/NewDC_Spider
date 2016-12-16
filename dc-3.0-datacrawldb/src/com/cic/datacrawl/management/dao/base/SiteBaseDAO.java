//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.Site;

public interface SiteBaseDAO {

	public Site[] getAllSite();

	public Site[] getAllSite(int startIndex, int limit);

	public Site getSite(long id);

	public Site[] getSites(long[] id);
	
	public long addSite(Site site);
	
	public long[] addSites(Site[] sites);
	
	public int deleteSite(long id);
	
	public int deleteSites(long[] id);
	
	public int saveSite(Site site);
	
	public int[] saveSites(final Site[] sites);
	
	public int count();
	
	public Site[] queryByQA(final long qaId );
	public Site[] queryByRW(final long rwId );
	public Site[] queryByType(final java.lang.String type );
	public Site[] queryByGroupId(final long groupId );
	public long countByQA(final long qaId);
	public long countByRW(final long rwId);
	public long countByType(final java.lang.String type);
	public long countByGroupId(final long groupId);
}

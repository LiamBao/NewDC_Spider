/*****************************************************************<br>
 * <B>FILE :</B> DBUtils.java <br>
 * <B>CREATE DATE :</B> 2012-3-19 <br>
 * <B>DESCRIPTION :</B> <br>
 *
 * <B>CHANGE HISTORY LOG</B><br>
 *---------------------------------------------------------------<br>
 * NO.  |  DATE |   NAME   |   REASON   |  DESCRIPTION           <br>
 *---------------------------------------------------------------<br>
 *          
 *****************************************************************<br>
 */
package com.cic.datacrawl.management.utils;

/**
 * <B>Function :</B> <br>
 * <B>General Usage :</B> <br>
 * <B>Special Usage :</B> <br>
 * 
 * @author : jean.jiang<br>
 * @since : 2012-3-19<br>
 * @version : v1.0
 */
public class DBUtils {
	public static String buildPageSQL(int startIndex, int limit, String sql) {
		StringBuffer paginationSQL = new StringBuffer();
		if (limit > 0) {
			if (startIndex > 0) {
				limit = startIndex + limit - 1;
			}
			paginationSQL.append(" SELECT * FROM ( ");
			paginationSQL.append(" SELECT temp.* ,ROWNUM num FROM ( ");
			paginationSQL.append(sql);
			paginationSQL.append("　) temp where ROWNUM <= " + limit);
			paginationSQL.append(" ) ");
			if (startIndex > 0) {
				paginationSQL.append("  WHERE　num >= " + startIndex);
			}

		} else {
			paginationSQL.append(sql);
		}
		return paginationSQL.toString();
	}
}

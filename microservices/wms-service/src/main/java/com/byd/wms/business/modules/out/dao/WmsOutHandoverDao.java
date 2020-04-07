package com.byd.wms.business.modules.out.dao;

import java.util.List;
import java.util.Map;

/**
 * 需求交接
 * @author ren.wei3
 *
 */
public interface WmsOutHandoverDao {

	public int updateHXTO(List<Map<String,Object>> params);
	
	public int updateHXDN(List<Map<String,Object>> params);
	
	public int updateHXMO(List<Map<String,Object>> params);
}

package com.byd.wms.business.modules.pda.dao;

import java.util.List;
import java.util.Map;

public interface PdaInventorySearchDao {
	
	public List<Map<String, Object>> queryMatnr(Map<String, Object> params);
	
	
	public List<Map<String, Object>> inventoryList(Map<String, Object> params);
	
}

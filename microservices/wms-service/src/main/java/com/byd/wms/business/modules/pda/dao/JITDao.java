package com.byd.wms.business.modules.pda.dao;

import java.util.List;
import java.util.Map;

public interface JITDao {
	
	public List<Map<String, Object>> JITScanLabel(Map<String, Object> params);
	
	public List<Map<String, Object>> JITScanDeliveryNo(Map<String, Object> params);
}

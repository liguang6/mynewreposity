package com.byd.wms.business.modules.out.dao;

import java.util.List;
import java.util.Map;

public interface DispatchingDeliveryDao {
	List<Map<String, Object>> list(Map<String, Object> params);

	Map<String, Object> getLabelInfo(Map<String, Object> params);
	
	void insertHead(Map<String, Object> head);

	void insertItem(List<Map<String, Object>> item);
}

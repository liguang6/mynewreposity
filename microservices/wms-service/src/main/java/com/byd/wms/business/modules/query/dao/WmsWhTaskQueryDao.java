package com.byd.wms.business.modules.query.dao;

import java.util.List;
import java.util.Map;

public interface WmsWhTaskQueryDao {

	List<Map<String, Object>> getTaskList(Map<String, Object> params);
	int getTaskCount(Map<String, Object> params);

}

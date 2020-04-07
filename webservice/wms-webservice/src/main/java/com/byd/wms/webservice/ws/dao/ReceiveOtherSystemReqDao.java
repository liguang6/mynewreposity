package com.byd.wms.webservice.ws.dao;

import java.util.List;
import java.util.Map;

public interface ReceiveOtherSystemReqDao {
	
	List<Map<String, Object>> selectOtherSystemREQ(Map<String, Object> params);
	
	void saveOtherSystemREQ(List<Map<String,Object>> params);
	
	int updateOtherSystemREQ(List<Map<String,Object>> params);
}

package com.byd.wms.business.modules.report.service;

import java.util.List;
import java.util.Map;

/** 
* 
*/

public interface EngineMaterialService {

	List<Map<String, Object>> queryProject(Map<String, Object> params);

	void saveProject();

	void saveStock();

	List<Map<String, Object>> list(Map<String, Object> params);



}

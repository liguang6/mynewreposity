package com.byd.wms.business.modules.config.service;

import java.util.Map;

import com.byd.utils.PageUtils;

public interface WmsCPlantToService {
	PageUtils queryPage(Map<String,Object> params);

	void save(Map<String, Object> params);

	void deletes(String ids);
}

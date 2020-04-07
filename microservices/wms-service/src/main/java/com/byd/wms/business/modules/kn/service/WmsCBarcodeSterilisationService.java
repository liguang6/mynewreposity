package com.byd.wms.business.modules.kn.service;

import com.byd.utils.PageUtils;

import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
public interface WmsCBarcodeSterilisationService {
	// 分页查询
		PageUtils queryPage(Map<String, Object> params);

		PageUtils queryOne(Map<String, Object> params);

    PageUtils saveCoreLabel(Map<String, Object> params);

//	PageUtils printCoreLabel(Map<String, Object> params);
}

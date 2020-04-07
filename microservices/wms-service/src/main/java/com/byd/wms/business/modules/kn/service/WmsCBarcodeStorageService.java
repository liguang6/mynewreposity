package com.byd.wms.business.modules.kn.service;

import com.byd.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 
 * 储位标签打印
 *
 */
public interface WmsCBarcodeStorageService {
	// 分页查询
		PageUtils queryPage(Map<String, Object> params);

		boolean importExel(List<Map<String, Object>> params);

}

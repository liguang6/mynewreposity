package com.byd.wms.business.modules.kn.dao;

import java.util.List;
import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
public interface WmsCBarcodeStorageDao {

	List<Map<String, Object>> queryBarcodeStorage(Map<String, Object> params);

	int getBarcodeStorageCount(Map<String, Object> params);
	
	List<Map<String, Object>> queryAll();
}

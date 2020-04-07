package com.byd.wms.business.modules.query.service;

import java.util.Map;
import com.byd.utils.PageUtils;
/**
 * 库内查询（冻结记录、盘点记录、移储记录）
 *
 * @author cscc tangj
 * @email 
 * @date 2018-11-21 15:15:08
 */
public interface WmsKnQueryService{
	// 查询冻结记录
    PageUtils queryFreezeRecordPage(Map<String, Object> params);
    // 查询盘点记录
    PageUtils queryInventoryPage(Map<String, Object> params);
    // 查询移储记录
    PageUtils queryStorageMovePage(Map<String, Object> params);
}


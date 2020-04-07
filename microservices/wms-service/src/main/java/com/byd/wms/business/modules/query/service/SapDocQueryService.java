package com.byd.wms.business.modules.query.service;

import java.util.Map;
import com.byd.utils.PageUtils;
/**
 * 查询事务记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-16 10:12:08
 */
public interface SapDocQueryService {
	// 事务记录分页查询
    PageUtils queryPage(Map<String, Object> params);
    
}


package com.byd.wms.business.modules.query.service;

import com.byd.utils.PageUtils;

import java.util.Map;

/**
 * 查询发料包装
 * @author qjm
 * @email
 * @date 2019-06-05
 */

public interface WmsDocPackingQueryService {
	//分页查询
    PageUtils queryPage(Map<String, Object> params);
    
}


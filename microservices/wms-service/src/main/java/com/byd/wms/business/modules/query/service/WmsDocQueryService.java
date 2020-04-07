package com.byd.wms.business.modules.query.service;

import java.util.Map;
import com.byd.utils.PageUtils;
/**
 * 查询事务记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-16 10:12:08
 */
public interface WmsDocQueryService {
	// 事务记录分页查询
    PageUtils queryPage(Map<String, Object> params);
    //收货日志
    PageUtils ReceiveLogPage(Map<String, Object> params);
    
    //收货日志-条码明细
    PageUtils ReceiveLabelPage(Map<String, Object> params);
    
}


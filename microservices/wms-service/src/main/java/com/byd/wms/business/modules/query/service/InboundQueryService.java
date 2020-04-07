package com.byd.wms.business.modules.query.service;

import java.util.Map;
import com.byd.utils.PageUtils;
/**
 * 进仓单查询
 * @author cscc tangj
 * @email 
 * @date 2018-11-29 09:55:02
 */
public interface InboundQueryService {

    PageUtils queryPage(Map<String, Object> params);
    // 查询明细
    PageUtils queryInboundItemPage(Map<String, Object> params);
    // 关闭操作
    boolean delete(Map<String,Object> params);
    
}


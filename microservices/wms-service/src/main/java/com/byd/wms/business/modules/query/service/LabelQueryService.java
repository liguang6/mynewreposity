package com.byd.wms.business.modules.query.service;

import java.util.Map;
import com.byd.utils.PageUtils;
/**
 * 标签查询
 * @author cscc tangj
 * @email 
 * @date 2018-11-28 09:15:08
 */
public interface LabelQueryService {

    PageUtils queryPage(Map<String, Object> params);
    
}


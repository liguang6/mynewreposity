package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCHandoverEntity;

import java.util.List;
import java.util.Map;

/**
 * 交接人员配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
public interface WmsCHandoverService extends IService<WmsCHandoverEntity>{

    PageUtils queryPage(Map<String, Object> params);
    
    List<Map<String, Object>> getCHandoverList(Map<String, Object> map);
   
}


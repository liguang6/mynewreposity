package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCHandoverTypeEntity;

import java.util.List;
import java.util.Map;

/**
 * 交接模式配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
public interface WmsCHandoverTypeService extends IService<WmsCHandoverTypeEntity>{

    PageUtils queryPage(Map<String, Object> params);
    
    List<Map<String, Object>> getList(Map<String, Object> map);
    

	int merge(List<Map<String,Object>> list);
   
}


package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCMatUrgentEntity;

import java.util.List;
import java.util.Map;

/**
 * 紧急物料配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-15 13:57:57
 */
public interface WmsCMatUrgentService extends IService<WmsCMatUrgentEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    /**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
}


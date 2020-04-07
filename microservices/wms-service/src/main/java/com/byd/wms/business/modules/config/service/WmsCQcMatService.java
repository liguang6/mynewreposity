package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCQcMatEntity;

import java.util.List;
import java.util.Map;

/**
 * 物料质检配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
public interface WmsCQcMatService extends IService<WmsCQcMatEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
	 * 导区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
}


package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCMatLtSampleEntity;

import java.util.List;
import java.util.Map;

/**
 * 物料物流参数配置表 自制产品入库参数
 *
 * @author cscc
 * @email 
 * @date 2018-09-28 10:30:07
 */
public interface WmsCMatLtSampleService extends IService<WmsCMatLtSampleEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	public int merge(List<Map<String,Object>> list);
}


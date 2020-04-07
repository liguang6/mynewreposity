package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;

public interface WmsSapPlantLgortService extends IService<WmsSapPlantLgortEntity>{
	PageUtils queryPage(Map<String,Object> params);
	/**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	public List<Map<String,Object>> selectLgortList(Map<String, Object> param);
}

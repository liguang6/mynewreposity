package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;

public interface WmsSapPlantService extends IService<WmsSapPlant>{
	
	PageUtils queryPage(Map<String,Object> params);
	

	List<WmsSapPlant> queryPlantListByMap(Map<String,Object> queryParams);
}

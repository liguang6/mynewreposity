package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCPlant;

public interface WmsCPlantService extends IService<WmsCPlant>{
	
	PageUtils queryPage(Map<String,Object> params);
	
	List<WmsCPlant> queryWmsCPlant(Map<String,Object> params);

}

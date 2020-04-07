package com.byd.wms.business.modules.config.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsSBestRuleEntity;

public interface WmsSBestRuleService extends IService<WmsSBestRuleEntity>{
	// 分页查询
	PageUtils queryPage(Map<String, Object> params);

}

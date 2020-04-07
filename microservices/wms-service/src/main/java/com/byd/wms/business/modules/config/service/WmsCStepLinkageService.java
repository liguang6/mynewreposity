package com.byd.wms.business.modules.config.service;

import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCStepLinkageEntity;
/**
 * 
 * 一步联动主数据配置
 *
 */
public interface WmsCStepLinkageService extends IService<WmsCStepLinkageEntity>{
	// 分页查询
		PageUtils queryPage(Map<String, Object> params);

}

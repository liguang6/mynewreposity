package com.byd.wms.business.modules.config.service;

import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCControlFlagEntity;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
/**
 * 
 * 控制标识配置
 *
 */
public interface WmsCControlFlagService extends IService<WmsCControlFlagEntity>{
	// 分页查询
		PageUtils queryPage(Map<String, Object> params);

}

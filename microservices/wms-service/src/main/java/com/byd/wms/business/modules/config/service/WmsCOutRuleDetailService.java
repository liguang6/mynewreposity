package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleDetailEntity;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleEntity;

import java.util.Map;

/**
 * 
 * 出库规则明细配置
 *
 */
public interface WmsCOutRuleDetailService extends IService<WmsCOutRuleDetailEntity>{
	// 分页查询
		PageUtils queryPage(Map<String, Object> params);

}

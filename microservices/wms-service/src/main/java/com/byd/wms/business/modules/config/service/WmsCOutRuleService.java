package com.byd.wms.business.modules.config.service;

import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleDetailEntity;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleEntity;
/**
 * 
 * 出库规则明细配置
 *
 */
public interface WmsCOutRuleService extends IService<WmsCOutRuleEntity>{
	// 分页查询
		PageUtils queryPage(Map<String, Object> params);

}

package com.byd.wms.business.modules.config.service;

import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
/**
 * 
 * 分配存储类型搜索顺序至控制标识
 *
 */
public interface WmsCControlSearchService extends IService<WmsCControlSearchEntity>{
	// 分页查询
		PageUtils queryPage(Map<String, Object> params);

}

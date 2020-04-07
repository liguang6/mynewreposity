package com.byd.wms.business.modules.config.service;

import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCControlFlagEntity;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;

/**
 * 
 * 
 *
 */
public interface WmsCEngineService {

	PageUtils queryPage(Map<String, Object> params);

	Map<String, Object> selectById(Map<String, Object> params);

	void updateById(Map<String, Object> params);

	void insert(Map<String, Object> params);

	void deleteById(Map<String, Object> params);

}

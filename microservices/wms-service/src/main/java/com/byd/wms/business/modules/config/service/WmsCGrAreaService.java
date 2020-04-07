package com.byd.wms.business.modules.config.service;

import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCGrAreaEntity;
/**
 * 收料房存放区配置
 * 
 * @author tangj
 * @email 
 * @date 2018年08月01日 
 */
public interface WmsCGrAreaService extends IService<WmsCGrAreaEntity>{
    // 分页查询
	PageUtils queryPage(Map<String, Object> params);
	
}

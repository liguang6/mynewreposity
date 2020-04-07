package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCQcPlantEntity;
import java.util.Map;

/**
 * 工厂质检配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
public interface WmsCQcPlantService extends IService<WmsCQcPlantEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


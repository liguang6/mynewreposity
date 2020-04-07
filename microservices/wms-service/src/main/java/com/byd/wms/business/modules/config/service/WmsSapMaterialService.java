package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import java.util.Map;

/**
 * 物料信息表 SAP同步获取
 *
 * @author cscc
 * @email 
 * @date 2018-08-14 08:45:52
 */
public interface WmsSapMaterialService extends IService<WmsSapMaterialEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


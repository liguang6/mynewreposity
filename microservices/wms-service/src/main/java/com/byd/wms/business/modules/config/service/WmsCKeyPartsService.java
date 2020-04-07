package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCKeyPartsEntity;
import java.util.List;
import java.util.Map;

/**
 * 工厂关键零部件配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
public interface WmsCKeyPartsService extends IService<WmsCKeyPartsEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    public int batchSave(List<WmsCKeyPartsEntity> list);
}


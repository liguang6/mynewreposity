package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCMatManagerTypeEntity;

import java.util.List;
import java.util.Map;

/**
 * 仓库人料关系模式配置
 *
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
public interface WmsCMatManagerTypeService extends IService<WmsCMatManagerTypeEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    public int batchSave(List<WmsCMatManagerTypeEntity> list);
    
    public List<Map<String,Object>>getLgortSelect(Map<String,Object> param);
}


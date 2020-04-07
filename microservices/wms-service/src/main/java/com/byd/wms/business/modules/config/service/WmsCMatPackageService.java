package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCHandoverEntity;

import java.util.List;
import java.util.Map;

/**
 * 物料包装规格配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
public interface WmsCMatPackageService{

    PageUtils queryPage(Map<String, Object> params);
    // 根据抬头ID获取明细数据
    public List<Map<String,Object>> getItemByHeadId(String head_id);
    
    public Map<String,Object> getHeadById(String head_id);
    // 保存或更新
    public boolean merge(Map<String, Object> params);
    
    public boolean delete(Map<String, Object> params);
   
}


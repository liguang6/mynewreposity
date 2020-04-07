package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;

/**
 * 质检结果配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
public interface WmsCQcResultService extends IService<WmsCQcResultEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    /**
     * 查询质检结果配置
     * @param werks
     * @param qcResult
     * @return
     */
    WmsCQcResultEntity queryQcResult(String werks,String qcResult);
    
    void saveCopyData(List<Map<String,Object>> list);
}


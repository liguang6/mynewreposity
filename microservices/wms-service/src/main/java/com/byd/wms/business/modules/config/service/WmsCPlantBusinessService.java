package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCPlantBusinessEntity;

import java.util.List;
import java.util.Map;

/**
 * 工厂业务类型配置表
 *
 * @author cscc
 * @email 
 * @date 2018-09-29 14:57:55
 */
public interface WmsCPlantBusinessService extends IService<WmsCPlantBusinessEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    Map<String,Object> getById(long id);
    // 获取工厂业务类型最大排序号
    public long getMaxSortNo(String werks);
    // 查询wms业务类型配置表
    List<Map<String,Object>> getWmsBusinessCode(String businessCode);
    
    public List<Map<String,Object>> validate(List<String> list);
    
    List<Map<String,Object>> getWmsBusinessCodeList(Map<String,Object> params);

    public void saveCopyData(List<Map<String,Object>> list);
}


package com.byd.wms.business.modules.config.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreStorageSearchEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @ClassName WmsCoreSearchSequenceDao
 * @Description  存储类型搜索顺序配置
 * @Author qiu.jiaming1
 * @Date 2019/2/28
 */
public interface WmsCoreStorageSearchDao extends BaseMapper<WmsCoreStorageSearchEntity> {
    List<Map<String,Object>> queryStorageSearchList(Map<String,Object> params);
    WmsCoreStorageSearchEntity selectStorageSearchById(Map idMap);
    int getStorageSearchCount(Map<String, Object> params);

    String queryAreaName(String warehouseCode,String storageAreaCode);

    List<Map<String,Object>> getStorageAreaSearch(@Param("warehouseCode") String warehouseCode);

    List<Map<String,Object>> getStorageAreaCode(@Param("warehouseCode")String warehouseCode);
}

package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCQcPlantEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 工厂质检配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
public interface WmsCQcPlantDao extends BaseMapper<WmsCQcPlantEntity> {
    
	List<Map<String,Object>> getListByPage(Map<String,Object> params);
	
	int getListCount(Map<String,Object> params);
}

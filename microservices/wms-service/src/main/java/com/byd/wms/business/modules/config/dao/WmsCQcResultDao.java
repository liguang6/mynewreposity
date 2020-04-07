package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 质检结果配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
public interface WmsCQcResultDao extends BaseMapper<WmsCQcResultEntity> {
	
    List<Map<String,Object>> getListByPage(Map<String,Object> params);
	
	int getListCount(Map<String,Object> params);
	
	// 保存复制数据
	int saveCopyData(List<Map<String,Object>> list);
	
	int updateCopyData(List<Map<String,Object>> list);
	
	int merge(List<Map<String,Object>> list);
}

package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCHandoverTypeEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 交接模式配置表
 * 
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
public interface WmsCHandoverTypeDao  extends BaseMapper<WmsCHandoverTypeEntity> {
	
	public List<Map<String,Object>> getListByPage(Map<String,Object> params);
	
	public int getListCount(Map<String,Object> params);
	
	List<Map<String, Object>> getList(Map<String, Object> map);
	
	int merge(List<Map<String,Object>> list);
}

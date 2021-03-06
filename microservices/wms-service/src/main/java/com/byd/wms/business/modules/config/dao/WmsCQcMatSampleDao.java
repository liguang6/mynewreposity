package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCQcMatSampleEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 物料质检抽样配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
public interface WmsCQcMatSampleDao extends BaseMapper<WmsCQcMatSampleEntity> {
	/**
	 * 区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	public void merge(List<WmsCQcMatSampleEntity> list);
}

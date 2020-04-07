package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCPlantBusinessEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 工厂业务类型配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-09-29 14:57:55
 */
public interface WmsCPlantBusinessDao extends BaseMapper<WmsCPlantBusinessEntity> {
	
	List<Map<String,Object>> getListByPage(Map<String,Object> params);
	
	int getListCount(Map<String,Object> params);
	
	Map<String,Object> getById(long id);
	
	public long getMaxSortNo(String werks);
	
	List<Map<String,Object>> getWmsBusinessCode(String businessCode);
	/**
	 * 区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	// 从WMS业务类型配置表获取 BusinessCode
	List<Map<String,Object>> getWmsBusinessCodeList(Map<String,Object> params);
	// 保存复制操作中新增的数据
	int saveCopyData(List<Map<String,Object>> list);
	// 更新复制操作中已存在的数据
	int updateCopyData(List<Map<String,Object>> list);
}

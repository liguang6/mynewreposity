package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCHandoverTypeEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 物料包装规格配置表
 * 
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
public interface WmsCMatPackageDao{
	
	public List<Map<String,Object>> getListByPage(Map<String,Object> params);
	
	public int getListCount(Map<String,Object> params);
	
	public int insertHead(Map<String,Object> params);
	// 批量insert
	public int insertItems(Map<String,Object> params);
	
    public int updateHead(Map<String,Object> params);
    // 批量update
	public int updateItems(Map<String,Object> params);
	
	public List<Map<String,Object>> getItemList(@Param("mat_package_head_id") String mat_package_head_id);

	public Map<String,Object> getHeadById(@Param("id") String id);
	
	public int deleteHead(@Param("headId") String headId);
	
	public int deleteItems(@Param("headId") String headId);
	
	public int deleteItem(@Param("id") String id);
	
}

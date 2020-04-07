package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.masterdata.entity.DeptEntity;

/**
 * 车间班组、小班组维护
 * 
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
public interface WorkgroupDao extends BaseMapper<DeptEntity> {
	// 模糊查找工序数据
	public List<Map<String,Object>> getProcessList(Map<String,String> map);
	
	public List<Map<String,Object>> getStandardWorkgroupList(Map<String,String> map);
	
	public List<Map<String,Object>> getWorkShopByCode(Map<String,String> map);
	
}

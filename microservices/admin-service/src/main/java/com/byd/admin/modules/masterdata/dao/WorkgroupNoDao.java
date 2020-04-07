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
public interface WorkgroupNoDao  {
	
	public List<Map<String,Object>> getStandardWorkgroupList(Map<String,String> map);
	public List<Map<String,Object>> getWorkShopByCode(Map<String,String> map);
	public Map<String,Object> getWorkGroupById(Map<String,String> map);
	
	public List<Map<String,Object>> getWorkGroupByCode(Map<String,String> map);
	public Map<String,Object> getWorkTeamById(Map<String,String> map);
	public Map<String,Object> getDeptWorkTeamById(Map<String,String> map);
	
	public List<Map<String,Object>> getWorkshopWorkgroupByCode(Map<String,Object> map);
	
	public List<Map<String,Object>> getWorkshopWorkgroupWorkTeamByCode(Map<String,Object> map);
	
	
}

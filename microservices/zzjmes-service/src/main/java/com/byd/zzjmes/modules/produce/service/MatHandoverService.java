package com.byd.zzjmes.modules.produce.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;
/**
 * 车间内交接、车间供货
 * @author tangj
 * @email 
 * @date 2019-09-23 15:12:08
 */
public interface MatHandoverService{
	
	public PageUtils queryPage(Map<String, Object> params);
	
	List<Map<String,Object>> getMatInfo(Map<String, Object> condMap);
	
	public void save(Map<String, Object> condMap);
	
    List<Map<String,Object>> getSupplyMatInfo(Map<String, Object> condMap);
	
	public void saveSupply(Map<String, Object> condMap);
	
	List<Map<String,Object>> getList(Map<String, Object> condMap);
	
	List<Map<String,Object>> getHandoverRuleList(Map<String, Object> condMap);
	
	public PageUtils querySupplyPage(Map<String, Object> params);
	
	List<Map<String,Object>> getSupplyDetailList(Map<String, Object> condMap);
	
}

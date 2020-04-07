package com.byd.zzjmes.modules.produce.dao;

import java.util.List;
import java.util.Map;

/**
 * 车间内交接/车间供货
 * @author tangj
 * @email 
 * @date 2019-09-23 15:12:08
 */
public interface MatHandoverDao{
	
	List<Map<String,Object>> getMatHandoverList(Map<String, Object> condMap);
	
	int getMatHandoverCount(Map<String, Object> condMap);
	
	List<Map<String,Object>> getMatInfo(Map<String, Object> condMap);
	
	public int save(Map<String, Object> condMap);
		
	List<Map<String,Object>> getSupplyMatInfo(Map<String, Object> condMap);
	
	public int saveSupply(Map<String, Object> condMap);
	
	List<Map<String,Object>> getHandoverRuleList(Map<String, Object> condMap);
	
	List<Map<String,Object>> getSupplyList(Map<String, Object> condMap);
	
	int getSupplyCount(Map<String, Object> condMap);
	
	List<Map<String,Object>> getSupplyDetailList(Map<String, Object> condMap);
}

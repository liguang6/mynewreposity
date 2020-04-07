package com.byd.zzjmes.modules.produce.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.R;
/**
 * 批次计划
 * @author thw
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface BatchPlanService{
	
	List<Map<String,Object>> getBatchList(Map<String, Object> condMap);
	List<Map<String,Object>> getOrderBatchList(Map<String, Object> condMap);
	
	Map<String,Object> getBacthPlanById(String id);
	
	List<Map<String,Object>> getMachinePlanByBatch(Map<String, Object> condMap);
	
	public int addBatchPlan(Map<String,Object> condMap);
	public R editBatchPlan(Map<String,Object> condMap);
	public R deleteBatchPlan(String id);
	
}

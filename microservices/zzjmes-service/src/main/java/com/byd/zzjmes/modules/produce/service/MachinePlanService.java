package com.byd.zzjmes.modules.produce.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;
/**
 * 机台计划
 * @author tangj
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface MachinePlanService{
	
	public PageUtils queryPage(Map<String, Object> params);

	List<Map<String,Object>> getMachinePlanList(Map<String,Object> param);
	
	// 导入保存
	void save(Map<String,Object> param);
	// 删除
	void del(Map<String,Object> param);
	// 获取计划信息
	Map<String,Object> getPlanByMap(Map<String,Object> param);
	// 本批次在整个订单中所属数量范围，的起始数
	int getZzjPlanBatchQuantity(Map<String,Object> param);
	// 查询下料明细是否存在
	List<Map<String,Object>> getExistPmdList(Map<String,Object> param);
	// 取已存在的机台计划数量列表 判断数量是否超出
	List<Map<String,Object>> checkMachinePlanList(Map<String,Object> params);
	// 导入时校验机台信息
	List<String> checkMachine(Map<String,Object> params);
	
	List<Map<String,Object>> getOutputRecords(Map<String,Object> params);
	
	List<Map<String,Object>> getTemplateData(Map<String,Object> params);
	
}

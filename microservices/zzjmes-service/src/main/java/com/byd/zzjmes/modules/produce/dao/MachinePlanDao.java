package com.byd.zzjmes.modules.produce.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
/**
 * 机台计划
 * @author tangj
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface MachinePlanDao{

	List<Map<String,Object>> getMachinePlanList(Map<String,Object> param);
	
	int getMachinePlanListCount(Map<String,Object> param);
	// 导入保存抬头
	int saveHead(Map<String,Object> param);
	// 导入保存行项目
	int saveItems(Map<String,Object> param);
	// 保存更新的行项目数据
	int saveItemsHistory(Map<String,Object> param);
	// 批量更新行项目数据
	int updateItems(Map<String,Object> param);
	// 删除行项目
	int deleteItems(@Param("delete_list") List<Map<String,Object>> itemList);
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
	// 获取抬头信息
	Map<String,Object> getHeadByMap(Map<String,Object> params);
	
	List<Map<String,Object>> getOutputRecords(Map<String,Object> params);
	
	List<Map<String,Object>> getTemplateData(Map<String,Object> params);
}

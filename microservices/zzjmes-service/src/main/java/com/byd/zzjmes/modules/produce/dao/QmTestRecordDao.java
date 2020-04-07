package com.byd.zzjmes.modules.produce.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
/**
 * 自制件品质检验
 * @author tangj
 * @email 
 * @date 2019-09-16 10:12:08
 */
public interface QmTestRecordDao{

	List<Map<String,Object>> getTestRecordList(Map<String,Object> param);
		// 导入保存抬头
	int saveHead(Map<String,Object> param);
	// 导入保存行项目
	int saveItems(Map<String,Object> param);
	// 批量更新抬头数据
	int updateHead(Map<String,Object> param);
	// 批量更新行项目数据
	int updateItems(Map<String,Object> param);
	// 删除抬头
	int deleteHead(Map<String,Object> param);
	// 删除行项目
	int deleteItems(Map<String,Object> param);
	// 获取需求数量
	List<Map<String,Object>> getPmdInfo(Map<String,Object> params);
//	// 本批次在整个订单中所属数量范围，的起始数
//	int getZzjPlanBatchQuantity(Map<String,Object> param);
	// 查询订单区域信息（用于获取检测规则，生产检测数据）
	List<Map<String,Object>> getOrderList(Map<String,Object> param);
	
	List<Map<String,Object>> getHeadList(Map<String,Object> params);

	int getHeadCount(Map<String,Object> params);
//	// 获取抬头信息
//	Map<String,Object> getHeadByMap(Map<String,Object> params);
}

package com.byd.wms.business.modules.kn.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * 盘点记录
 * 
 * @author tangj
 * @email 
 * @date 2018-10-19 10:12:08
 */
public interface WmsKnInventoryDao{
	// 分页查询盘点记录（抬头）
	List<Map<String,Object>> getInventoryList(Map<String,Object> param);
	//分页查询盘点记录（抬头）记录条数
	int getInventoryCount(Map<String,Object> param);
	// 保存抬头
	int saveInventoryHead(Map<String,Object> param);
	// 保存明细
	int saveInventoryItem(List<Map<String,Object>> list);
	// 根据盘点任务号查询盘点表抬头
	Map<String,Object> getInventoryHead(Map<String,Object> param);
	// 根据盘点任务号查询盘点记录行项目明细
	List<Map<String,Object>> getInventoryItemList(String inventoryNo);
	// 查询库存数据，生成盘点表数据 
	List<Map<String,Object>> getInventoryStockList(Map<String,Object> param);
	// 更新盘点表status
	int updateStatus(Map<String,Object> param);
	// 批量更新盘点结果录入（初盘、复盘）
	int batchUpdateInventory(List<Map<String,Object>> list);
	// 验证盘点任务号的行项目数据是否都录入完整
	List<Map<String,Object>> getExistsItemList(Map<String,Object> param);
	// 盘点确认数据查询  
	List<Map<String,Object>> getInventoryConfirmList(Map<String,Object> param);
	// 获取区域管理员
	List<Map<String,Object>> getWhManagerList(Map<String,Object> param);
}

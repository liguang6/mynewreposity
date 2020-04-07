package com.byd.wms.business.modules.query.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;

/**
 * 退货单查询
 * @author cscc tangj
 * @email 
 * @date 2018-11-30 08:52:18
 */
public interface ReturngoodsQueryDao {

	List<Map<String,Object>> getReceiveRoomOutList(Map<String,Object> param);
	
	int getReceiveRoomOutCount(Map<String,Object> param);
	
	List<Map<String,Object>> getReturngoodsItemList(Map<String,Object> map);
	
	int getReturngoodsItemCount(Map<String,Object> map);
	
	int updateHead(Map<String,Object> map);
	
	int updateItem(Map<String,Object> map);
	
	public int updateItemDetail(Map<String,Object> map);
	
	int updateOutPicking(Map<String,Object> map);
	
	int batchUpdateStock(List<Map<String,Object>> list);
	// 退货单类型
	List<Map<String,Object>> getReturnDocTypeList ();
	// 退货类型
	List<Map<String,Object>> getReturnTypeList (@Param("type") String type);
	
	public int getReturnItemDoneCount(Map<String,Object> param);
	public List<Map<String,Object>> getReturnDetailList(Map<String,Object> param);
	public List<Map<String,Object>> getStockInfo(Map<String,Object> param);
	public int updateStockInfo(Map<String,Object> param);
}

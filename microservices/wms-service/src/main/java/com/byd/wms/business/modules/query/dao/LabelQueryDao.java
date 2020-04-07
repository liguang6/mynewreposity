package com.byd.wms.business.modules.query.dao;

import java.util.List;
import java.util.Map;

/**
 * 标签查询
 * @author cscc tangj
 * @email 
 * @date 2018-11-28 09:12:08
 */
public interface LabelQueryDao {

	List<Map<String,Object>> getLabelList(Map<String,Object> param);
	
	int getLabelCount(Map<String,Object> param);
	// 查询收货单号BY采购订单号
	List<String> getReceiptNoByPoNo(Map<String,Object> param);
	// 查询进仓单号BY生产订单
	List<String> getInboundNoByPoNo(Map<String,Object> param);
}

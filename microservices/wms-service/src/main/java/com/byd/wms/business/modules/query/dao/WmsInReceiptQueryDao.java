package com.byd.wms.business.modules.query.dao;

import java.util.List;
import java.util.Map;

/**
 * 查询收料房的物料操作记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-15 10:12:08
 */
public interface WmsInReceiptQueryDao {
	// 收货单（来料跟踪）
	List<Map<String,Object>> getReceiptInfoList(Map<String,Object> param);
	// 收货单（来料跟踪）记录条数
	int getReceiptInfoCount(Map<String,Object> param);
	//从采购订单表获取需求跟踪号
	List<Map<String,Object>> getPOITEMBednr(Map<String,Object> param);
	//库存查询
	List<Map<String, Object>> getStockInfoList(Map<String, Object> params);
	int getStockInfoCount(Map<String, Object> params);
	int queryBasicDataUnsyncCount(Map<String, Object> params);
	List<Map<String, Object>> queryBasicDataUnsync(Map<String, Object> params);
	
	List<Map<String, Object>> getLabelData(Map<String, Object> params);
}

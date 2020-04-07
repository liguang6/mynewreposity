package com.byd.wms.business.modules.query.service;

import java.util.List;
import java.util.Map;
import com.byd.utils.PageUtils;
/**
 * 查询收料房的物料操作记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-15 10:12:08
 */
public interface WmsInReceiptQueryService {
	// 收货单（来料跟踪）
    PageUtils queryPage(Map<String, Object> params);
    //从采购订单表获取需求跟踪号
  	List<Map<String,Object>> getPOITEMBednr(Map<String,Object> param);
  	
  	//库存查询
  	PageUtils queryStockPage(Map<String, Object> params);
  	/**
  	 * 主数据异常查询
  	 * @param params
  	 * @return
  	 */
	PageUtils queryBasicDataUnsyncPage(Map<String, Object> params);
	
	List<Map<String, Object>> getLabelData(Map<String, Object> params);
}


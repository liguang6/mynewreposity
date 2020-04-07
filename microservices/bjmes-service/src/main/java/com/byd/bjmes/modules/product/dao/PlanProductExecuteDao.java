package com.byd.bjmes.modules.product.dao;

import java.util.List;
import java.util.Map;

public interface PlanProductExecuteDao {

	List<Map<String, Object>> getOrderProducts(Map<String, Object> params);

	List<Map<String, Object>> getProductPlanList(Map<String, Object> params);
	
	void saveProductPlan(List<Map<String,Object>> plan_list);
	
	void updateProductPlan(List<Map<String,Object>> plan_list);

	List<Map<String, Object>> getProcessFlow(Map<String, Object> params);

	Map<String, Object> getLastScanInfo(Map<String, Object> params);

	List<Map<String, Object>> getProdSubParts(Map<String, Object> params);

	List<Map<String, Object>> getPDProductList(Map<String, Object> params);

	void savePDScanInfo(Map<String, Object> _scan_info);

	void savePDKey_Parts(List<Map> subParts_list);

	Map<String, Object> getProductInfo(Map<String, Object> params);

	void savePDProductsInfo(Map _scan_info);

	List<Map<String, Object>> getProductByKeyparts(Map<String, Object> params);

	List<Map<String, Object>> getProductPDList(Map<String, Object> params);
	
}

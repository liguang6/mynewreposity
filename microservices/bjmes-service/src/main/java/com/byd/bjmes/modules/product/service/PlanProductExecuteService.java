package com.byd.bjmes.modules.product.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.R;

public interface PlanProductExecuteService {

	R getOrderProducts(Map<String, Object> params);

	List<Map<String,Object>> getProductPlanList(Map<String, Object> params);

	R saveProductPlan(Map<String, Object> params);

	R getScanProductInfo(Map<String, Object> params);

	R getPDProductList(Map<String, Object> params);

	R saveProductScan(Map<String, Object> params);

	R getProductByKeyparts(Map<String, Object> params);

	R getProductPDList(Map<String, Object> params);
	
}

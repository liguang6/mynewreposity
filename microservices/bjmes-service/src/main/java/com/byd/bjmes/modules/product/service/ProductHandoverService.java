package com.byd.bjmes.modules.product.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;
/**
 * @author tangj
 * @email 
 * @date 2019-10-23 15:12:08
 */
public interface ProductHandoverService{
	
	public PageUtils queryPage(Map<String, Object> params);
	
	List<Map<String,Object>> getProductInfo(Map<String, Object> condMap);
	
	public void save(Map<String, Object> condMap);
	
	List<Map<String,Object>> checkProductInfo(String product_no);
	
}

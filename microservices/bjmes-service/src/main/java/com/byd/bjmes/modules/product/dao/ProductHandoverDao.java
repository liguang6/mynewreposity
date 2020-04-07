package com.byd.bjmes.modules.product.dao;

import java.util.List;
import java.util.Map;

/**
 * 产品交接
 * @author tangj
 * @email 
 * @date 2019-10-23 15:12:08
 */
public interface ProductHandoverDao{
	
	List<Map<String,Object>> getHandoverList(Map<String, Object> condMap);
	
	int getHandoverCount(Map<String, Object> condMap);
	
	List<Map<String,Object>> getProductInfo(Map<String, Object> condMap);
	
	public int save(Map<String, Object> condMap);
		
}

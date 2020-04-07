package com.byd.wms.business.modules.report.dao;

import java.util.List;
import java.util.Map;

/** 
 * 报表查询组件
* @author 作者 : ren.wei3@byd.com 
* @version 创建时间：2019年6月4日 下午5:07:33 
*/

public interface QueryComponentDao {

	//库存查询
	List<Map<String, Object>> getStockInfoList(Map<String, Object> params);
	
	int getStockInfoCount(Map<String, Object> params);
}

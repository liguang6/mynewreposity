package com.byd.wms.business.modules.report.dao;

import java.util.List;
import java.util.Map;

/**
 * 账龄月库存
 * 
 * @author chen.yafei1
 * @email 
 * @date 2019-11-28
 */
public interface AgingMonthlyReportDao {
	
	//查询呆滞库存  getAgingMonthlyInfoCount
	List<Map<String,Object>> getAgingMonthlyInfoList(Map<String,Object> param);
	//查询呆滞库存	查询信息记录条数	
	int getAgingMonthlyInfoCount(Map<String,Object> param);

}

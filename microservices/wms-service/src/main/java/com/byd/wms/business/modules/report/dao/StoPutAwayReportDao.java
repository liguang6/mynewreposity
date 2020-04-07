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
public interface StoPutAwayReportDao {
	
	//查询STO在途数据
	List<Map<String,Object>> getStoPutAwayInfoList(Map<String,Object> param);
	//查询STO在途数据	查询信息记录条数	
	int getStoPutAwayInfoCount(Map<String,Object> param);

}

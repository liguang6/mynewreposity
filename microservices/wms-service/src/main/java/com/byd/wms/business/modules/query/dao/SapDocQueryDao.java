package com.byd.wms.business.modules.query.dao;

import java.util.List;
import java.util.Map;

/**
 * 查询事务记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-16 10:12:08
 */
public interface SapDocQueryDao {

	List<Map<String,Object>> getDocList(Map<String,Object> param);
	// 事务记录条数
	int getDocCount(Map<String,Object> param);
}

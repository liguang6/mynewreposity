package com.byd.wms.business.modules.query.dao;

import java.util.List;
import java.util.Map;

/**
 * 查询发料包装
 * @author qjm
 * @email
 * @date 2019-06-05
 */
public interface WmsDocPackingQueryDao {

	List<Map<String,Object>> getDocPackingList(Map<String, Object> param);
	int getDocPackingCount(Map<String, Object> param);
	List <Map<String,Object>> getPackingByLabelNo(List list);

}

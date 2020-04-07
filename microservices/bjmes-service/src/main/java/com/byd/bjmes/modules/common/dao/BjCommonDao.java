package com.byd.bjmes.modules.common.dao;

import java.util.List;
import java.util.Map;

public interface BjCommonDao {

	List<Map<String, Object>> getOrderList(Map<String, Object> params);

	int getOrderListTotalCount(Map<String, Object> params);

}

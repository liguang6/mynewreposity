package com.byd.zzjmes.modules.order.dao;

import java.util.List;
import java.util.Map;

public interface OrderDao {
	public List<Map<String, Object>> getOrderList(Map<String, Object> params);
	public List<Map<String, Object>> getOrderListSelect(Map<String, Object> params);
	public int getOrderListTotalCount(Map<String, Object> params);
	public int addOrder(Map<String, Object> params);
	public int editOrder(Map<String, Object> params);
	public int delOrder(Map<String, Object> params);
	public String queryMaxOrderNo(Map<String, Object> params);
}

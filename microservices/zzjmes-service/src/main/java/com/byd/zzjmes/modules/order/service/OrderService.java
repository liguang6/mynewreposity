package com.byd.zzjmes.modules.order.service;

import java.util.Map;
import com.byd.utils.PageUtils;

public interface OrderService {
	public PageUtils getOrderList(Map<String, Object> params);
	public int addOrder(Map<String, Object> params);
	public int editOrder(Map<String, Object> params);
	public int delOrder(Map<String, Object> params);
	public String getNextOrderNoByYear(Map<String, Object> params);
}

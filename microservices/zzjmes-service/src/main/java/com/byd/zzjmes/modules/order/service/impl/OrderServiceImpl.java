package com.byd.zzjmes.modules.order.service.impl;


import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.zzjmes.modules.order.dao.OrderDao;
import com.byd.zzjmes.modules.order.service.OrderService;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private OrderDao orderDao;

	@Override
	public PageUtils getOrderList(Map<String, Object> params) {
		Page<Map<String, Object>> roderPage = new Page<Map<String, Object>>();	
		int pageSize = Integer.valueOf(params.get("pageSize") != null?params.get("pageSize").toString():"15");
		int pageNo=Integer.valueOf(params.get("pageNo") != null?params.get("pageNo").toString():"1");
		params.put("start", (pageNo - 1) * pageSize);
		roderPage.setRecords(orderDao.getOrderList(params));
		roderPage.setSize(pageSize);
		roderPage.setTotal(orderDao.getOrderListTotalCount(params));
		PageUtils page = new PageUtils(roderPage);		
		return page;
	}
	
	@Override
	public int addOrder(Map<String, Object> params) {
		logger.info("---->orderService::addOrder");
		return orderDao.addOrder(params);
	}
	
	@Override
	public int editOrder(Map<String, Object> params) {
		return orderDao.editOrder(params);
	}
	
	@Override
	public int delOrder(Map<String, Object> params) {
		return orderDao.delOrder(params);
	}
	
	@Override
	public String getNextOrderNoByYear(Map<String, Object> params){
		String productive_year = params.get("productive_year").toString();
		String order_no = orderDao.queryMaxOrderNo(params);
		String new_order_no = "";
		if (order_no == null){
			return "Z" + productive_year + "001";
		}
		int serial = Integer.parseInt(order_no.substring(5, 8)) + 1;
		if (serial < 10){
			new_order_no = order_no.substring(0, 5) + "00" + String.valueOf(serial);
		}else if (serial < 100){
			new_order_no = order_no.substring(0, 5) + "0" + String.valueOf(serial);
		}else{
			new_order_no = order_no.substring(0, 5) + String.valueOf(serial);
		}
		return new_order_no.replace("D","Z");
	}

}

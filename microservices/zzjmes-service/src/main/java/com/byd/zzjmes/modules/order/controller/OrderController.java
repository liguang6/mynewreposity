package com.byd.zzjmes.modules.order.controller;

import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.order.service.OrderService;

/**
 * 订单管理
 * @author Yangke
 * @date 2019-09-02
 */
@RestController
@RequestMapping("order")
public class OrderController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private OrderService orderService;
    @Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/getOrderList")
    public R getOrderList(@RequestParam Map<String, Object> params){
		logger.info("-->OrderController getOrderList");
		return R.ok().put("page", orderService.getOrderList(params));
	}
	
	@RequestMapping("/getOrderListSelect")
    public R getOrderListSelect(@RequestParam Map<String, Object> params){
		return R.ok().put("result", orderService.getOrderList(params));
	}
	
	@RequestMapping("/addOrder")
	public R addOrder(@RequestParam Map<String, Object> params){
    	String orderNo = orderService.getNextOrderNoByYear(params);
    	params.put("order_no", orderNo);
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("creator", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
    	params.put("creat_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		int result = orderService.addOrder(params);
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/editOrder")
	public R editOrder(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
		params.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
    	params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		int result = orderService.editOrder(params);
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/delOrder")
	public R delOrder(@RequestParam Map<String, Object> params){		
		int result = orderService.delOrder(params);
		return R.ok().put("result", result);
	}

}

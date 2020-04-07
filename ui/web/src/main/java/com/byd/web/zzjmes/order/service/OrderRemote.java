package com.byd.web.zzjmes.order.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
/**
 * 下料明细编辑
 * @author Yangke
 * @date 2019-09-05
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface OrderRemote {
	
	@RequestMapping(value = "/zzjmes-service/order/getOrderList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getOrderList(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/order/addOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R addOrder(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/order/editOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R editOrder(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/order/delOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delOrder(@RequestParam Map<String, Object> paramMap);
}

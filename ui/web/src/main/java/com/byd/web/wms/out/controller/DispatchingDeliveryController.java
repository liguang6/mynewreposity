package com.byd.web.wms.out.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.out.service.DispatchingDeliveryServiceRemote;

@RestController
@RequestMapping("/out/delivery")
public class DispatchingDeliveryController {
	
	@Autowired
	private DispatchingDeliveryServiceRemote dispatchingDeliveryServiceRemote;
	

	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		return dispatchingDeliveryServiceRemote.list(params);
	}
	
	@RequestMapping("/delivery")
	public R delivery(@RequestBody List<Map<String, Object>> params) {
		return dispatchingDeliveryServiceRemote.delivery(params);
	}
}

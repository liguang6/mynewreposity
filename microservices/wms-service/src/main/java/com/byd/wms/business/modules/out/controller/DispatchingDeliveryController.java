package com.byd.wms.business.modules.out.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.out.service.DispatchingDeliveryService;

@RestController
@RequestMapping("/out/delivery")
public class DispatchingDeliveryController {
	@Autowired
	private DispatchingDeliveryService dispatchingDeliveryService;

	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = dispatchingDeliveryService.list(params);
		return R.ok().put("page", page);
	}

	@RequestMapping("/delivery")
	public R delivery(@RequestBody List<Map<String, Object>> params) {
		try {
			String message = dispatchingDeliveryService.delivery(params);
			if (StringUtils.isNotBlank(message))
				return R.ok().put("msg", message);
			else
				return R.ok().put("msg", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return R.error();
		}
	}
}

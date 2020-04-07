package com.byd.web.wms.query.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.WmsWhTaskQueryRemote;

@RestController
@RequestMapping("query/taskQuery")
public class WmsWhTaskQueryController {

	@Autowired
	private WmsWhTaskQueryRemote wmsWhTaskQuery;

	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		return wmsWhTaskQuery.list(params);
	}
}

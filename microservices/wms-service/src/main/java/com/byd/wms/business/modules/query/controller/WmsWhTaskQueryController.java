package com.byd.wms.business.modules.query.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.WmsWhTaskQueryService;

@RestController
@RequestMapping("query/taskQuery")
public class WmsWhTaskQueryController {
	@Autowired
	private WmsWhTaskQueryService wmsWhTaskQuery;
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsWhTaskQuery.queryTaskPage(params);
        return R.ok().put("page", page);
    }

}

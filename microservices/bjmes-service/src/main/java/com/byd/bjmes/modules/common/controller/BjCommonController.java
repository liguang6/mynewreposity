package com.byd.bjmes.modules.common.controller;

import java.util.Map;

import com.byd.bjmes.modules.common.service.BjCommonService;
import com.byd.utils.R;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("common")
public class BjCommonController {
    @Autowired
    private BjCommonService bjCommonService;
    
    @RequestMapping("/getOrderList")
    public R getOrderList(@RequestBody Map<String, Object> params){
		return R.ok().put("page", bjCommonService.getOrderList(params));
	}
}
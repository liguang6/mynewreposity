package com.byd.web.bjmes.common.controller;

import java.util.Map;

import com.byd.utils.R;
import com.byd.web.bjmes.common.service.BjCommonRemote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bjmes/common")
public class BjCommonController {
    @Autowired
    private BjCommonRemote zzjCommonRemote;

    @RequestMapping("/getOrderList")
    public R getOrderList(@RequestParam Map<String, Object> paramMap){
		return zzjCommonRemote.getOrderList(paramMap);
	}
}
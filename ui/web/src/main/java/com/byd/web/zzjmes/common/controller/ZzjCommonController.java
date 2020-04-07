package com.byd.web.zzjmes.common.controller;

import java.util.Map;

import com.byd.utils.R;
import com.byd.web.zzjmes.common.service.ZzjCommonRemote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用Controller
 *
 */
@RestController
@RequestMapping("zzjmes/common")
public class ZzjCommonController {
    @Autowired
    private ZzjCommonRemote zzjCommonRemote;
    
    @RequestMapping("/getPlanBatchList")
    public R getPlanBatchList(@RequestParam Map<String,Object> params) {
    	return zzjCommonRemote.getPlanBatchList(params);
    }

    @RequestMapping("/getJTProcess")
    public R getJTProcess(@RequestParam Map<String,Object> params) {
    	return zzjCommonRemote.getJTProcess(params);
    }
    
    @RequestMapping("/getMachineList")
    public R getMachineList(@RequestParam Map<String,Object> params) {
    	return zzjCommonRemote.getMachineList(params);
    }
    
    @RequestMapping("/getAssemblyPositionList")
    public R getAssemblyPositionList(@RequestParam Map<String,Object> params) {
    	return zzjCommonRemote.getAssemblyPositionList(params);
    }
    
    @RequestMapping("/productionExceptionManage")
    public R productionExceptionManage(@RequestParam Map<String,Object> params) {
    	return zzjCommonRemote.productionExceptionManage(params);
    }
    @RequestMapping("/getProductionExceptionList")
    public R getProductionExceptionList(@RequestParam Map<String,Object> params) {
    	return zzjCommonRemote.getProductionExceptionList(params);
    }
    
}
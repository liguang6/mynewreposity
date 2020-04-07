package com.byd.web.zzjmes.produce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.zzjmes.produce.service.BatchPlanRemote;

/**
 * 批次计划
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("zzjmes/batchPlan")
public class BatchPlanController {
    @Autowired
    private BatchPlanRemote batchPlanRemote;
    
    @RequestMapping("/getBatchList")
 	public R getBatchList(@RequestParam Map<String,Object> params){
    	return batchPlanRemote.getBatchList(params);
 	}
    
    @RequestMapping("/getOrderBatchList")
  	public R getOrderBatchList(@RequestParam Map<String,Object> params){
  		return batchPlanRemote.getOrderBatchList(params);
  	}
    
    
	@RequestMapping("/addBatchPlan")
	public R addBatchPlan(@RequestParam Map<String,Object> params) {
		return batchPlanRemote.addBatchPlan(params);
	}

	@RequestMapping("/editBatchPlan")
	public R editBatchPlan(@RequestParam Map<String,Object> params) {
		return batchPlanRemote.editBatchPlan(params);
	}

    @RequestMapping("/getMachinePlanByBatch")
  	public R getMachinePlanByBatch(@RequestParam Map<String,Object> params){
    	return batchPlanRemote.getMachinePlanByBatch(params);
  	}
	
	@RequestMapping("/deleteBatchPlan")
	public R deleteBatchPlan(@RequestParam Map<String,Object> params) {
		return batchPlanRemote.deleteBatchPlan(params);
	}
    
}
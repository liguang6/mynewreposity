package com.byd.web.zzjmes.produce.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 批次计划
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface BatchPlanRemote {
    
    @RequestMapping(value = "/zzjmes-service/batchPlan/getBatchList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
 	public R getBatchList(@RequestParam Map<String,Object> params);
    
    @RequestMapping(value = "/zzjmes-service/batchPlan/getOrderBatchList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  	public R getOrderBatchList(@RequestParam Map<String,Object> params);
    
	@RequestMapping(value = "/zzjmes-service/batchPlan/addBatchPlan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R addBatchPlan(@RequestParam Map<String,Object> params) ;

	@RequestMapping(value = "/zzjmes-service/batchPlan/editBatchPlan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R editBatchPlan(@RequestParam Map<String,Object> params) ;

    @RequestMapping(value = "/zzjmes-service/batchPlan/getMachinePlanByBatch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  	public R getMachinePlanByBatch(@RequestParam Map<String,Object> params);
	
	@RequestMapping(value = "/zzjmes-service/batchPlan/deleteBatchPlan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deleteBatchPlan(@RequestParam Map<String,Object> params) ;
}
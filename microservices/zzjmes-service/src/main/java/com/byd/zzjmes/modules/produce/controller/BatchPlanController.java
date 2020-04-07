package com.byd.zzjmes.modules.produce.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.produce.service.BatchPlanService;

/**
 * 批次计划
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("batchPlan")
public class BatchPlanController {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private BatchPlanService batchPlanService;
    @Autowired
    private UserUtils userUtils;
    
    @RequestMapping("/getBatchList")
 	public R getBatchList(@RequestParam Map<String,Object> params){
 		return R.ok().put("data", batchPlanService.getBatchList(params));
 	}
    
    @RequestMapping("/getOrderBatchList")
  	public R getOrderBatchList(@RequestParam Map<String,Object> params){
  		return R.ok().put("data", batchPlanService.getOrderBatchList(params));
  	}
    
    
	@RequestMapping("/addBatchPlan")
	public R addBatchPlan(@RequestParam Map<String,Object> params) {
		Map<String,Object> user = userUtils.getUser();
/*		String batch = params.get("batch").toString();
		
		int batch_i = Integer.valueOf(batch);
		batch = "P" + String.format("%03d", batch_i);
		params.put("batch", batch);*/
		int from_no = 1;
		int to_no = Integer.valueOf( params.get("quantity").toString() );
		List<Map<String,Object>> batchPlanList = batchPlanService.getOrderBatchList(params);
		if( batchPlanList.size() >0 ) {
			from_no = Integer.valueOf( batchPlanList.get(batchPlanList.size()-1).get("to_no").toString()) +1;
			to_no += Integer.valueOf( batchPlanList.get(batchPlanList.size()-1).get("to_no").toString());
		}
		params.put("from_no", from_no);
		params.put("to_no", to_no);
		params.put("edit_date",DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		params.put("editor", user.get("USERNAME")+":"+user.get("FULL_NAME"));
		params.put("status", "00");

		try {
			int result = batchPlanService.addBatchPlan(params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return R.error("保存失败！" + e.getMessage());
		}
		return R.ok();
	}

	@RequestMapping("/editBatchPlan")
	public R editBatchPlan(@RequestParam Map<String,Object> params) {
		Map<String,Object> user = userUtils.getUser();
		String id = params.get("id").toString();
		params.put("edit_date",DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		params.put("editor", user.get("USERNAME")+":"+user.get("FULL_NAME"));
		
		return batchPlanService.editBatchPlan(params);
	}
	
    @RequestMapping("/getMachinePlanByBatch")
  	public R getMachinePlanByBatch(@RequestParam Map<String,Object> params){
  		return R.ok().put("data", batchPlanService.getMachinePlanByBatch(params));
  	}
	

	@RequestMapping("/deleteBatchPlan")
	public R deleteBatchPlan(@RequestParam Map<String,Object> params) {
		String id = params.get("id").toString();
		return batchPlanService.deleteBatchPlan(id);
	}
    
}
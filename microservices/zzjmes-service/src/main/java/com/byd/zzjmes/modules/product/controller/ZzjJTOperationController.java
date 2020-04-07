package com.byd.zzjmes.modules.product.controller;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.zzjmes.modules.product.service.ZzjJTOperationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("jtOperation")
public class ZzjJTOperationController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	ZzjJTOperationService operationService;
	
	@RequestMapping("/getJTPlan")
	public R getJTPlan(@RequestBody Map<String, Object> params) {
		List<Map<String,Object>> datalist=operationService.getJTPlan(params);
		PageUtils page=new PageUtils(new Page().setRecords(datalist));
		return R.ok().put("page", page);		
	}
	
	@RequestMapping("/getPmdItems")
	public R getPmdItems(@RequestBody Map<String, Object> params) {
		R r = operationService.getPmdItems(params);
		
		return r;		
	}
	
	@RequestMapping("/getMachineAchieve")
	public R getMachineAchieve(@RequestBody Map<String, Object> params) {
		R r = operationService.getMachineAchieve(params);	
		return r;		
	}
	
	@RequestMapping("/saveJTBindData")
	public R saveJTBindData(@RequestBody Map<String, Object> params) {
		R r = operationService.saveJTBindData(params);	
		return r;	
	}
	
	@RequestMapping("/getPmdOutputItems")
	public R getPmdOutputItems(@RequestBody Map<String, Object> params) {
		R r = operationService.getPmdOutputItems(params);	
		return r;	
	}
	@RequestMapping("/saveJTOutputData")
	public R saveJTOutputData(@RequestBody Map<String, Object> params) {
		R r = operationService.saveJTOutputData(params);	
		return r;	
	}
	
	@RequestMapping("/startOpera")
	public R startOpera(@RequestBody Map<String, Object> params) {
		R r = operationService.startOpera(params);	
		return r;	
	}
	
	@RequestMapping("/queryOutputRecords")
	public R queryOutputRecords(@RequestBody Map<String, Object> params){
		PageUtils page= operationService.queryOutputRecords(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/getPmdAbleQty")
	public R getPmdAbleQty(@RequestBody Map<String, Object> params) {
		R r = operationService.getPmdAbleQty(params);	
		return r;	
	}
	
	@RequestMapping("/savePmdOutQty")
	public R savePmdOutQty(@RequestBody Map<String, Object> params) {
		R r = operationService.savePmdOutQty(params);
		return r;
	}
	
	@RequestMapping("/deletePmdOutInfo")
	public R deletePmdOutInfo(@RequestBody Map<String, Object> params) {
		R r = operationService.deletePmdOutInfo(params);
		return r;
	}
	
	@RequestMapping("/scrapePmdOutInfo")
	public R scrapePmdOutInfo(@RequestBody Map<String, Object> params) {
		R r = operationService.scrapePmdOutInfo(params);
		return r;
	}
	
	@RequestMapping("/queryCombRecords")
	public R queryCombRecords(@RequestBody Map<String, Object> params) {
		PageUtils page= operationService.queryCombRecords(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/getPmdBaseInfo")
	public R getPmdBaseInfo(@RequestBody Map<String, Object> params){
		R r = operationService.getPmdBaseInfo(params);	
		return r;
	}

	@RequestMapping("/checkBindPlan")
	public R checkBindPlan(@RequestBody Map<String, Object> params){
		R r = operationService.checkBindPlan(params);	
		return r;
	}
}

package com.byd.bjmes.modules.product.controller;

import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.bjmes.modules.product.service.PlanProductExecuteService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/product/pe")
public class PlanProductExecuteController {
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private PlanProductExecuteService ppExecuteService;

	@RequestMapping("/getOrderProducts")
	public R getOrderProducts(@RequestBody Map<String,Object> params) {
		return ppExecuteService.getOrderProducts(params);
	}
	
	@RequestMapping("/getProductPlanList")
	public R getProductPlanList(@RequestBody Map<String,Object> params) {
		
		PageUtils page=new PageUtils(new Page().setRecords(ppExecuteService.getProductPlanList(params)));
		return R.ok().put("page", page);		
	}
	
	@RequestMapping("/saveProductPlan")
	public R saveProductPlan(@RequestBody Map<String,Object> params) {
		Map<String,Object> u = userUtils.getUser();
		String create_date=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		params.put("creator", u.get("FULL_NAME")+"("+u.get("STAFF_NUMBER")+")");
		params.put("create_date", create_date);
		
		return ppExecuteService.saveProductPlan(params);
	}

	@RequestMapping("/getScanProductInfo")
	public R getScanProductInfo(@RequestBody Map<String,Object> params) {
		
		return ppExecuteService.getScanProductInfo(params);
	}

	@RequestMapping("/getPDProductList")
	public R getPDProductList(@RequestBody Map<String,Object> params) {
		return ppExecuteService.getPDProductList(params);
	}
	
	@RequestMapping("/saveProductScan")
	public R saveProductScan(@RequestBody Map<String,Object> params) {
		return ppExecuteService.saveProductScan(params);
	}

	@RequestMapping("/getProductByKeyparts")
	public R getProductByKeyparts(@RequestBody Map<String,Object> params){
		return ppExecuteService.getProductByKeyparts(params);
	}

	@RequestMapping("/getProductPDList")
	public R getProductPDList(@RequestBody Map<String,Object> params) {
		return ppExecuteService.getProductPDList(params);
	}
}

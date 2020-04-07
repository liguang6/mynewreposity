package com.byd.web.bjmes.product.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.bjmes.product.service.PlanProductExecuteRemote;

@RestController
@RequestMapping("/bjmes/product/pe")
public class PlanProductExecuteController {
	@Autowired
    private UserUtils userUtils;
	@Autowired
	private PlanProductExecuteRemote ppeRemote;
	
	/**
     * 查询订单产品列表
     */
    @RequestMapping("/getOrderProducts")
    public R queryPage(@RequestParam Map<String, Object> params){
        return ppeRemote.getOrderProducts(params);
    }
    
    @RequestMapping("/getProductPlanList")
    public R getProductPlanList(@RequestParam Map<String, Object> params) {
    	return ppeRemote.getProductPlanList(params);
    }
    
    @RequestMapping("/saveProductPlan")
    public R saveProductPlan(@RequestParam Map<String, Object> params) {
    	return ppeRemote.saveProductPlan(params);
    }

    @RequestMapping("/getScanProductInfo")
    public R getScanProductInfo(@RequestParam Map<String, Object> params){
        return ppeRemote.getScanProductInfo(params);
    }

    @RequestMapping("/getPDProductList")
    public R getPDProductList(@RequestParam Map<String, Object> params) {
        return ppeRemote.getPDProductList(params);
    }

    @RequestMapping("/saveProductScan")
    public R saveProductScan(@RequestParam Map<String, Object> params){
        return ppeRemote.saveProductScan(params);
    }

    @RequestMapping("/getProductByKeyparts")
    public R getProductByKeyparts(@RequestParam Map<String,Object> params) {
        return ppeRemote.getProductByKeyparts(params);
    }

    @RequestMapping("/getProductPDList")
    public R getProductPDList(@RequestParam Map<String,Object> params) {
        return ppeRemote.getProductPDList(params);
    }
}

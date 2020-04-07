package com.byd.wms.business.modules.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.report.service.QueryComponentService;

/** 
 * WMS库存与ERP库存比较
* @author 作者 : ren.wei3@byd.com 
* @version 创建时间：2019年6月4日 下午5:20:33 
* 
*/

@RestController
@RequestMapping("report/stockCompare")
public class StockCompareController {
	
	@Autowired
    private QueryComponentService queryComponentService;
    
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = queryComponentService.queryStockPage(params);
        return R.ok().put("page", page);
    }
}

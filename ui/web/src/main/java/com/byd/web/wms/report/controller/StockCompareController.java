package com.byd.web.wms.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.report.service.QueryComponentRemote;

/** 
 * WMS库存与ERP库存比较
* @author 作者 : ren.wei3@byd.com 
* @version 创建时间：2019年6月4日 下午3:22:20 
* 
*/

@RestController
@RequestMapping("report/stockCompare")
public class StockCompareController {

	@Autowired QueryComponentRemote queryComponentRemote;
	
	/**
	 * WMS库存与ERP库存比较
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return queryComponentRemote.listStock(params);
	}
}

package com.byd.wms.business.modules.report.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.report.service.StagnateStockReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName
 * @Author chen.yafei1
 * @Date 2019年11月27日
 * @Description //TODO $end$
 **/

@RestController
@RequestMapping("report/stagnateStockReport")
public class StagnateStockReportController {

	@Autowired private StagnateStockReportService stagnateStockReportService;
	
	/**
	 * 呆滞库存报表
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		System.out.println(params.toString());
    	PageUtils page = stagnateStockReportService.querystagenateStockPage(params);
		return R.ok().put("page", page);
	}
}

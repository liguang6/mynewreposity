package com.byd.web.wms.report.controller;

import com.byd.utils.R;
import com.byd.web.wms.report.service.StagnateStockReportRemote;

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

	@Autowired private StagnateStockReportRemote stagnateStockReportRemote;
	
	/**
	 * 呆滞库存报表
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return stagnateStockReportRemote.querystagenateStockPage(params);
	}
}

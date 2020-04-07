package com.byd.web.wms.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.report.service.AgingMonthlyReportRemote;

/**
 * @ClassName
 * @Author chen.yafei1
 * @Date 2019年11月28日
 * @Description //TODO $end$
 **/

@RestController
@RequestMapping("report/agingMonthlyReport")
public class AgingMonthlyReportController {
	@Autowired
	private AgingMonthlyReportRemote agingMonthlyReportRemote;
	/**
	 * 账龄月报表
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return agingMonthlyReportRemote.queryAgingMonthlyPage(params);
	}

}

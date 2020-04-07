package com.byd.wms.business.modules.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.report.service.StoPutAwayReportService;

/**
 * @ClassName
 * @Author chen.yafei1
 * @Date 2019年11月28日
 * @Description //TODO $end$
 **/

@RestController
@RequestMapping("report/stoPutAwayReport")
public class StoPutAwayReportController {
	@Autowired
	private StoPutAwayReportService stoPutAwayReportService;
	/**
	 * 账龄月报表
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		System.out.println(params.toString());
    	PageUtils page = stoPutAwayReportService.queryStoPutAwayReportPage(params);
		return R.ok().put("page", page);
	}

}

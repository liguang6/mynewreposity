package com.byd.web.wms.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.report.service.StoPutAwayReportRemote;

/**
 * @ClassName
 * @Author chen.yafei1
 * @Date 2019年12月3日
 * @Description //TODO $end$
 **/

@RestController
@RequestMapping("report/stoPutAwayReport")
public class StoPutAwayReportController {
	@Autowired
	private StoPutAwayReportRemote stoPutAwayReportRemote;
	/**
	 * STO在途报表
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return stoPutAwayReportRemote.queryStoPutAwayPage(params);
	}

}

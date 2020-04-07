package com.byd.web.wms.report.controller;

import com.byd.utils.R;
import com.byd.web.wms.report.service.QueryComponentRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName
 * @Author yang.bintao
 * @Date 2019年11月22日
 * @Description //TODO $end$
 **/

@RestController
@RequestMapping("report/wmsReportBarcodeLog")
public class WmsReportBarcodeLogController {

	@Autowired
	private QueryComponentRemote queryComponentRemote;

	/**
	 * 扫描条码记录表查询
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		return queryComponentRemote.wmsReportBarcodeLogList(params);
	}
}

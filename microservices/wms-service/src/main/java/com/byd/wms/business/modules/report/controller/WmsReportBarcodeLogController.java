package com.byd.wms.business.modules.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.report.service.WmsReportBarcodeLogService;

/** 
 *    扫描条码记录表查询
* @author 作者 : yang.bintao@byd.com 
* @version 创建时间：2019年11月21日 下午5:20:33 
* 
*/

@RestController
@RequestMapping("report/wmsReportBarcodeLog")
public class WmsReportBarcodeLogController {
	
	@Autowired
    private WmsReportBarcodeLogService wmsReportBarcodeLogService;
    
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsReportBarcodeLogService.queryWmsReportBarcodeLogPage(params);
        return R.ok().put("page", page);
    }
}

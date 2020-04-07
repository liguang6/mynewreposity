package com.byd.wms.business.modules.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.WmsDocQueryService;

/** 
 * @author 作者 E-mail: ren.wei3@byd.com
 * @version 创建时间：2019年11月21日 
 * S收货日志查询
 */

@RestController
@RequestMapping("query/receivelogs")
public class ReceiveLogQueryController {
	
	@Autowired
    private WmsDocQueryService wmsDocQueryService;

	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsDocQueryService.ReceiveLogPage(params);
        return R.ok().put("page", page);
    }
	
	@RequestMapping("/listitem")
    public R listitem(@RequestParam Map<String, Object> params){
		PageUtils page = wmsDocQueryService.ReceiveLabelPage(params);
		return R.ok().put("page", page);
	}
}

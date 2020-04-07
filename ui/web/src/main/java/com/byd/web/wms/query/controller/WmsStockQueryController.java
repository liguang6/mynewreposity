package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.WmsInReceiptQueryRemote;

@RestController
@RequestMapping("query/stockQuery")
public class WmsStockQueryController {
	@Autowired
    private WmsInReceiptQueryRemote wmsInReceiptQueryRemote;
	
	 /**
     * 查询收货单记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsInReceiptQueryRemote.listStock(params);
    }
	
}

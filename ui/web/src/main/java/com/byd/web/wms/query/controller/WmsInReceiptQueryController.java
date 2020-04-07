package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.web.wms.query.service.WmsInReceiptQueryRemote;

/**
 * 查询收料房的物料操作记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-15 10:12:08
 */

@RestController
@RequestMapping("query/receiptQuery")
public class WmsInReceiptQueryController {
    @Autowired
    private WmsInReceiptQueryRemote wmsInReceiptQueryRemote;
   
    /**
     * 查询收货单记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsInReceiptQueryRemote.list(params);
    }
    /**
     * 打印
     */
    @RequestMapping("/getLabelData")
    public R getLabelData(@RequestParam Map<String, Object> params){
    	return wmsInReceiptQueryRemote.getLabelData(params);
    }
}

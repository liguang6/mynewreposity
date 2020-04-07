package com.byd.wms.business.modules.query.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.WmsInReceiptQueryService;

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
    private WmsInReceiptQueryService wmsInReceiptQueryService;
   
    /**
     * 查询收货单记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsInReceiptQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 打印数据
     */
    @RequestMapping("/getLabelData")
    public R getLabelData(@RequestParam Map<String, Object> params){
    	List<String> receiptNoList =Arrays.asList(params.get("receiptNoList").toString().split(","));
    	params.put("receiptNoList", receiptNoList);
    	List<Map<String,Object>> list = wmsInReceiptQueryService.getLabelData(params);
        return R.ok().put("data", list);
    }
}

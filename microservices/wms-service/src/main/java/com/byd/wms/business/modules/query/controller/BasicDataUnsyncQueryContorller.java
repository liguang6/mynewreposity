package com.byd.wms.business.modules.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.WmsInReceiptQueryService;

@RestController
@RequestMapping("query/basicDataUnsync")
public class BasicDataUnsyncQueryContorller {
	@Autowired
    private WmsInReceiptQueryService wmsInReceiptQueryService;
	
	/**
     * 查询收货单记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsInReceiptQueryService.queryBasicDataUnsyncPage(params);
        return R.ok().put("page", page);
    }
}

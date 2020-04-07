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
 * 查询事务记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-16 10:12:08
 */

@RestController
@RequestMapping("query/docQuery")
public class WmsDocQueryController {
    @Autowired
    private WmsDocQueryService wmsDocQueryService;
   
    /**
     * 查询事务记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsDocQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
}

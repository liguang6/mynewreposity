package com.byd.wms.business.modules.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.SapDocQueryService;

/**
 * 查询SAP凭证记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-21 10:12:08
 */

@RestController
@RequestMapping("query/sapDocQuery")
public class SapDocQueryController {
    @Autowired
    private SapDocQueryService sapDocQueryService;
   
    /**
     * 查询SAP凭证记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = sapDocQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
}

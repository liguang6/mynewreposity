package com.byd.wms.business.modules.query.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.WmsDocPackingQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 查询发料包装
 * @author qjm
 * @email 
 * @date 2019-06-05
 */

@RestController
@RequestMapping("query/docPackingQuery")
public class WmsDocPackingQueryController {
    @Autowired
    private WmsDocPackingQueryService wmsDocPackingQueryService;
   
    /**
     * 查询发料包装
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsDocPackingQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
}

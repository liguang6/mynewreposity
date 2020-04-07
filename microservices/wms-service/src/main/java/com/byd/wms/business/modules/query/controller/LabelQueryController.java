package com.byd.wms.business.modules.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.LabelQueryService;

/**
 * 查询标签记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-28 10:02:08
 */

@RestController
@RequestMapping("query/labelQuery")
public class LabelQueryController {
    @Autowired
    private LabelQueryService labelQueryService;
   
    /**
     * 查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = labelQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
}

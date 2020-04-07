package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.SapDocQueryRemote;

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
    private SapDocQueryRemote sapDocQueryRemote;
   
    /**
     * 查询SAP凭证记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return sapDocQueryRemote.list(params);
    }
}

package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.LabelQueryRemote;

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
    private LabelQueryRemote labelQueryRemote;
   
    /**
     * 查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return labelQueryRemote.list(params);
    }
}

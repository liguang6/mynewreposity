package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.BasicDataSyncQueryRemote;
/**
 * 
 * @author xjw
 *
 */

@RestController
@RequestMapping("query/basicDataUnsync")
public class BasicDataSyncQueryController {
	@Autowired
	private BasicDataSyncQueryRemote bdsqRemote;
	
	 /**
     * 查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return bdsqRemote.list(params);
    }
}

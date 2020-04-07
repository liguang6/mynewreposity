package com.byd.web.wms.query.controller;

import com.byd.utils.R;
import com.byd.web.wms.query.service.WmsDocPackingQueryRemote;
import com.byd.web.wms.query.service.WmsDocQueryRemote;
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
    private WmsDocPackingQueryRemote wmsDocPackingQueryRemote;
   
    /**
     * 查询发料包装记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsDocPackingQueryRemote.list(params);
    }
}

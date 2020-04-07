package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.InboundQueryRemote;

/**
 * 查询进仓单记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-29 10:02:38
 */

@RestController
@RequestMapping("query/inboundQuery")
public class InboundQueryController {
    @Autowired
    private InboundQueryRemote inboundQueryRemote;
   
    /**
     * 查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return inboundQueryRemote.list(params);
    }
    /**
     * 明细
     */
    @RequestMapping("/detail")
    public R detail(@RequestParam Map<String, Object> params){
        return inboundQueryRemote.detail(params);
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestParam Map<String, Object> params){
    	return inboundQueryRemote.delete(params);
    }
}

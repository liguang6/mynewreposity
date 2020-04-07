package com.byd.web.wms.in.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.wms.in.service.WmsInboundRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年1月17日 下午2:00:08 
 * 类说明 
 */
@RestController
@RequestMapping("in/wmsinbound")
public class WmsInInboundController {
	@Autowired
	WmsInboundRemote wmsInboundRemote;
	@Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return wmsInboundRemote.list(params);
	}
	
    /**
     * 根据工厂、仓库号、仓管员获取待进仓的来料任务清单
     * @param params
     * @return
     */
    @RequestMapping("/getInboundTasks")
    public R getInboundTasks(@RequestParam Map<String, Object> params){
    	return wmsInboundRemote.getInboundTasks(params);
    }
	
	/**
     * 获取仓管员
     * @param params
     * @return
     */
    @RequestMapping("/relatedAreaNamelist")
    public R RelatedAreaNamelist(@RequestParam Map<String, Object> params){
        return wmsInboundRemote.RelatedAreaNamelist(params);
    }
    /**
     * 获取库位
     */
    @RequestMapping("/lgortlist")
    public R lgortlist(@RequestParam Map<String, Object> params){
    	return wmsInboundRemote.lgortlist(params);
    }
    
    /**
     * 保存
     * @throws Exception 
     * @throws JSONException 
     */
    @RequestMapping("/save")
    public R saveInbound(@RequestParam Map<String, Object> params) throws Exception {
    	Map<String,Object> currentUser = userUtils.getUser();
   		params.put("USERNAME", currentUser.get("USERNAME"));
   		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
    	return wmsInboundRemote.saveInbound(params);
    }
    
    /**
     * 根据收货单获取收货单列表
     * @param params
     * @return
     */
    @RequestMapping("/Receiptlist")
    public R Receiptlist(@RequestParam Map<String, Object> params){
        return wmsInboundRemote.Receiptlist(params);
    }
    
    @RequestMapping("/sapComponentlist")
    public R sapComponentlist(@RequestParam Map<String, Object> params){
        return wmsInboundRemote.sapComponentlist(params);
    }

}

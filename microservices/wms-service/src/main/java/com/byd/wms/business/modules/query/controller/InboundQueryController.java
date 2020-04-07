package com.byd.wms.business.modules.query.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.query.service.InboundQueryService;

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
    private InboundQueryService inboundQueryService;
    @Autowired
    private RedisUtils redisUtils;
   
    /**
     * 查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = inboundQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 明细
     */
    @RequestMapping("/detail")
    public R detail(@RequestParam Map<String, Object> params){
    	PageUtils page = inboundQueryService.queryInboundItemPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestParam Map<String, Object> params){
    	String uid = UUID.randomUUID().toString();
    	String inboundNo = params.get("INBOUND_NO").toString();
    	if (redisUtils.tryLock(inboundNo, uid))
		{
    		boolean isOk=inboundQueryService.delete(params);
    		redisUtils.unlock(inboundNo, uid);
    		if(isOk) {
        		return R.ok();
        	}else {
        		return R.error();
        	}
		} else {
			return R.error(inboundNo+",正在执行中，请稍后再试！");	
		}
    	
    	
    	
    }
}

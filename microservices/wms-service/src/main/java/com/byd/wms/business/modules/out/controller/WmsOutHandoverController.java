package com.byd.wms.business.modules.out.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.out.service.WmsOutHandoverService;

/**
 * 需求交接控制器
 * @author ren.wei3
 *
 */
@RestController
@RequestMapping("/out/handover")
public class WmsOutHandoverController {

	@Autowired
	private WmsOutHandoverService wmsOutHandoverService;
	
	@Autowired
    private RedisUtils redisUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsOutHandoverService.list(params);
        return R.ok().put("page", page);
    }
    
    /**
	 * 需求交接
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/save")
	public R handover(@RequestParam Map<String, Object> params)  {
		Map<String, Object> retMap=new HashMap<String, Object>();
		try{
			String requirementNo = params.get("REQUIREMENT_NO").toString();
    		String uid = UUID.randomUUID().toString();
    		if (redisUtils.tryLock(requirementNo, uid))
    		{
    			params.put("UID", uid);
    			retMap = wmsOutHandoverService.handover(params);
    			redisUtils.unlock(requirementNo, uid);
    		} else {
    			return R.error(requirementNo+",正在执行中，请稍后再试！");	
    		}
			return R.ok().put("msg", retMap.get("msg"));
		} catch (Exception e) {
			return R.error("系统异常："+e.getMessage());	
		}	
	}
}

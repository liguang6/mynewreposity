package com.byd.web.wms.in.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.in.service.WmsInAutoPutawayRemote;

/**
 * 
 * @author ren.wei3
 * 一步联动收货
 */
@RestController
@RequestMapping("in/autoPutaway")
public class WmsInAutoPutawayController {

	@Autowired 
	WmsInAutoPutawayRemote wmsInAutoPutawayRemote;
	
	@Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
   		params.put("USERNAME", currentUser.get("USERNAME"));
   		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInAutoPutawayRemote.list(params);
	}
	
	@RequestMapping("/createPO")
	public R createPO(@RequestParam Map<String, Object> params)  {
		return wmsInAutoPutawayRemote.createPO(params);
	}
	
	@RequestMapping("/createDN")
	public R createDN(@RequestParam Map<String, Object> params)  {
		return wmsInAutoPutawayRemote.createDN(params);
	}
	
	@RequestMapping("/postDN")
	public R postDN(@RequestParam Map<String, Object> params)  {
		return wmsInAutoPutawayRemote.postDN(params);
	}
	
	@RequestMapping("/loglist")
	public R loglist(@RequestParam Map<String, Object> params){
		return wmsInAutoPutawayRemote.loglist(params);
	}
	
	@RequestMapping("/autoPutawayProcess")
	public R autoPutawayProcess(@RequestParam Map<String, Object> params){
		return wmsInAutoPutawayRemote.autoPutawayProcess(params);
	}
}

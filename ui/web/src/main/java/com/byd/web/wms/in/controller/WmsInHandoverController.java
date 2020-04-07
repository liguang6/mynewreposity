package com.byd.web.wms.in.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.wms.in.service.WmsInHandoverRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年1月18日 下午2:36:32 
 * 类说明 
 */
@RestController
@RequestMapping("in/wmsinhandoverbound")
public class WmsInHandoverController {

	@Autowired
	private WmsInHandoverRemote wmsInHandoverRemote;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
   		params.put("USERNAME", currentUser.get("USERNAME"));
   		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInHandoverRemote.list(params);
	}
	
	/**
	 * 进仓交接
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/save")
	public R handover(@RequestParam Map<String, Object> params)  {
		Map<String,Object> currentUser = userUtils.getUser();
   		params.put("USERNAME", currentUser.get("USERNAME"));
   		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInHandoverRemote.handover(params);
	}
	
	@RequestMapping("/labellist")
	public R labelList(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
   		params.put("USERNAME", currentUser.get("USERNAME"));
   		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInHandoverRemote.labelList(params);
	}
	
	@RequestMapping("/ValidlabelQyt")
	public R ValidlabelQyt(@RequestParam Map<String, Object> params){
		return wmsInHandoverRemote.ValidlabelQyt(params);
	}
	
	@RequestMapping("/inPoCptConsumelist")
	public R inPoCptConsumelist(@RequestParam Map<String, Object> params){
		return wmsInHandoverRemote.inPoCptConsumelist(params);
	}
	
	@RequestMapping("/ValidlabelHandover")
	public R ValidlabelHandover(@RequestParam Map<String, Object> params){
		return wmsInHandoverRemote.ValidlabelHandover(params);
	}
}

package com.byd.web.wms.query.controller;

import com.byd.utils.R;
import com.byd.web.wms.query.service.YPTDeliveryQueryRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** 
 * @author rain
 * @version 创建时间：2019年11月25日09:12:49
 * @description 云平台送货单查询
 */
@RestController
@RequestMapping("query/yptquery")
public class YPTDeliveryQueryController {
	@Autowired
	private YPTDeliveryQueryRemote yptDeliveryQueryRemote;
	
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return yptDeliveryQueryRemote.list(params);
	}
	
	@RequestMapping("/listitem")
    public R listitem(@RequestParam Map<String, Object> params){
		return yptDeliveryQueryRemote.listitem(params);
	}

	@RequestMapping("/yptdeliverbarlist")
	public R yptdeliverbarlist(@RequestParam Map<String, Object> params){
		return yptDeliveryQueryRemote.yptdeliverbarlist(params);
	}
	
	@RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
		return yptDeliveryQueryRemote.update(params);
	}
}

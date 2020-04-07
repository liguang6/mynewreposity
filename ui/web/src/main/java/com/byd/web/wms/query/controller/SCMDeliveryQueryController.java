package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.SCMDeliveryQueryRemote;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年3月6日 上午10:23:35 
 * 类说明 
 */
@RestController
@RequestMapping("query/scmquery")
public class SCMDeliveryQueryController {
	@Autowired
	private SCMDeliveryQueryRemote scmDeliveryQueryRemote;
	
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return scmDeliveryQueryRemote.list(params);
	}
	
	@RequestMapping("/listitem")
    public R listitem(@RequestParam Map<String, Object> params){
		return scmDeliveryQueryRemote.listitem(params);
	}
	
	@RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
		return scmDeliveryQueryRemote.update(params);
	}
}

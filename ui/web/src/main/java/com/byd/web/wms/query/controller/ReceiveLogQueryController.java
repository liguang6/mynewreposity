package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.ReceiveLogQueryRemote;

/** 
 * @author 作者 E-mail: ren.wei3@byd.com
 * @version 创建时间：2019年11月21日 
 * S收货日志查询
 */

@RestController
@RequestMapping("query/receivelogs")
public class ReceiveLogQueryController {

	@Autowired
	private ReceiveLogQueryRemote receiveLogQueryRemote;
	
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return receiveLogQueryRemote.list(params);
	}
	
	@RequestMapping("/listitem")
    public R listitem(@RequestParam Map<String, Object> params){
		return receiveLogQueryRemote.listitem(params);
	}
}

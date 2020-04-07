package com.byd.web.wms.in.controller;

import com.byd.utils.R;
import com.byd.web.wms.in.service.WmsWebserviceLogRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** 
 * @author rain
 * @version 创建时间：2019年11月27日16:24:29
 * @description 接口日志
 */
@RestController
@RequestMapping("in/webservicelog")
public class WmsWebserviceLogController {
	@Autowired
	private WmsWebserviceLogRemote wmsWebserviceLogRemote;

	/**
	 * 列表查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return wmsWebserviceLogRemote.list(params);
	}

	/**
	 * 重新触发
	 * @param pkLog
	 * @param model
	 * @return
	 * @throws IllegalAccessException
	 */
	@RequestMapping("/retrigger")
	public String retrigger(Long pkLog, Model model) throws IllegalAccessException{

		R r= wmsWebserviceLogRemote.retrigger(pkLog);
		return "/wms/in/wms_webservice_log";
	}

	@RequestMapping("/retriggerLogs")
    public R retriggerLogs(@RequestParam Map<String, Object> params){
		return wmsWebserviceLogRemote.retriggerLogs(params);
	}
}

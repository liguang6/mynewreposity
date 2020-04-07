package com.byd.web.wms.qc.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.wms.qc.service.WmsQcServiceRemote;

@RestController
@RequestMapping("/qcQuery")
public class WmsQcQueryController {
	
	@Autowired
	WmsQcServiceRemote qcServerRemote;
	
	@RequestMapping("/inspectionList")
	public R inspectionList(@RequestParam Map<String,Object> params){
		return qcServerRemote.inspectionList(params);
	}
	
	@RequestMapping("/inspectionItem")
	public R inspectionItem(@RequestParam Map<String,Object> params){
		return qcServerRemote.inspectionItem(params);
	}
	
	@RequestMapping("/resultList")
	public R resultList(@RequestParam Map<String,Object> params){
		return qcServerRemote.resultList(params);
	}
	
	@RequestMapping("/recordList")
	public R recordList(@RequestParam Map<String,Object> params){
		return qcServerRemote.recordList(params);
	}
	
	@RequestMapping("/destroyQtyList")
	public R destroyQtyList(@RequestParam Map<String,Object> params){
		return qcServerRemote.destroyQtyList(params);
	}

}

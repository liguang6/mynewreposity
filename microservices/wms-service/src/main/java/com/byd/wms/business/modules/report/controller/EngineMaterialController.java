package com.byd.wms.business.modules.report.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.wms.business.modules.report.service.EngineMaterialService;

import oracle.net.aso.r;

/** 
* 
* 
*/

@RestController
@RequestMapping("report/engineMaterial")
public class EngineMaterialController {

	@Autowired EngineMaterialService engineMaterialService;
	

	@RequestMapping("/queryProject")
    public List<Map<String, Object>> queryProject(@RequestParam Map<String, Object> params){
		return  engineMaterialService.queryProject(params);
	}
	
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return  R.ok().put("data", engineMaterialService.list(params));
	}
}

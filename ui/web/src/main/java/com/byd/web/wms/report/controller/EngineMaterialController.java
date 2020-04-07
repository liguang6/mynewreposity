package com.byd.web.wms.report.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.report.service.EngineMaterialRemote;

/** 
* 
* 
*/

@RestController
@RequestMapping("report/engineMaterial")
public class EngineMaterialController {

	@Autowired EngineMaterialRemote engineMaterialRemote;
	
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return engineMaterialRemote.list(params);
	}
}

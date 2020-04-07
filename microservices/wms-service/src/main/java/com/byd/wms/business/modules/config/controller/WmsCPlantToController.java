package com.byd.wms.business.modules.config.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.service.WmsCPlantToService;

@RestController
@RequestMapping("/config/cPlantTo")
public class WmsCPlantToController {
	@Autowired
	WmsCPlantToService wmsCPlantToService;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmsCPlantToService.queryPage(params);	
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/save")
	public R save(@RequestBody Map<String, Object> params) {
		try {
			wmsCPlantToService.save(params);
		}catch(Exception e) {
			return R.error().put("msg", e.getMessage());
		}	
		return R.ok();
	}
	
	@RequestMapping("/deletes")
	public R deletes(@RequestParam String ids) {
		try {
			wmsCPlantToService.deletes(ids);
		}catch(Exception e) {
			return R.error().put("msg", e.getMessage());
		}	
		return R.ok();
	}
}

package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCWhEntity;
import com.byd.web.wms.config.service.WmsCPlantToRemote;

@RestController
@RequestMapping("/config/cPlantTo")
public class WmsCPlantToController {
	@Autowired
    private UserUtils userUtils;
	@Autowired
	private WmsCPlantToRemote wmscPlantToRemote;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return wmscPlantToRemote.list(params);	
	}
	
	@RequestMapping("/save")
	public R save(@RequestParam Map<String,Object> params){
		//validate
		params.put("UPDATER", userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		params.put("UPDAT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		wmscPlantToRemote.save(params);
		return R.ok();
	}
	
	@RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		
		return wmscPlantToRemote.deletes(ids);
	}
	
}

package com.byd.web.wms.common.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.wms.common.service.WmsCDocNoRemote;

@RestController
@RequestMapping("/common/docNo")
public class WmsCDocNoController {
	@Autowired
	WmsCDocNoRemote wmsCDocNoRemote;
    @Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return wmsCDocNoRemote.list(params);
	}
	
	@RequestMapping("/listEntity")
	public R queryEntity(@RequestParam Map<String, Object> params){
		return wmsCDocNoRemote.queryEntity(params);
	}
	
	@RequestMapping("/save")
	public R add(@RequestParam Map<String, Object> params){
		Map<String,Object> user = userUtils.getUser();
	   	params.put("USERNAME", user.get("USERNAME"));
	   	params.put("FULL_NAME", user.get("FULL_NAME"));
		
		return wmsCDocNoRemote.add(params);
	}
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCDocNoRemote.info(id);
    }
    
    @RequestMapping("/update")
	public R update(@RequestParam Map<String, Object> params){
		Map<String,Object> user = userUtils.getUser();
	   	params.put("USERNAME", user.get("USERNAME"));
	   	params.put("FULL_NAME", user.get("FULL_NAME"));
		
		return wmsCDocNoRemote.update(params);
	}
    
    @RequestMapping("/del")
	public R delete(Long id){
		if(id == null){
			return R.error("参数错误");
		}
		return wmsCDocNoRemote.delete(id);
	}
	@RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		return wmsCDocNoRemote.deletes(ids);
	}
	
	@RequestMapping("/getdocno")
	public R getdocno(@RequestParam Map<String, Object> params){
		return wmsCDocNoRemote.getdocno(params);
	}
}

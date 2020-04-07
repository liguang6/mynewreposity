package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCSapUserEntity;
import com.byd.web.wms.config.service.WmsCSapUserRemote;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月29日 上午11:26:44 
 * 类说明 
 */
@RestController
@RequestMapping("/config/CSapUser")
public class WmsCSapUserController {
	@Autowired
	WmsCSapUserRemote wmscSapUserRemote;
	@Autowired
	UserUtils userUtils;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return wmscSapUserRemote.list(params);	
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmscSapUserRemote.info(id);
    }
	@RequestMapping("/update")
	public R update(@RequestBody WmsCSapUserEntity entity){
		//validate
		entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
		wmscSapUserRemote.update(entity);
		return R.ok();
	}
	
	@RequestMapping("/save")
	public R add(@RequestBody WmsCSapUserEntity entity){
		//validate
		entity.setDel("0");
		entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
		wmscSapUserRemote.add(entity);
		return R.ok();
	}
	
	@RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			WmsCSapUserEntity entity = new WmsCSapUserEntity();
			entity.setId(Long.parseLong(id[i]));
			entity.setDel("1");
			wmscSapUserRemote.update(entity);
		}
		return R.ok();
	}
}

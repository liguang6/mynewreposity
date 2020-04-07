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
import com.byd.web.wms.config.entity.WmsCControlFlagEntity;
import com.byd.web.wms.config.service.WmsCControlFlagRemote;

/**
 * 
 * 控制标识配置
 *
 */
@RestController
@RequestMapping("config/controlflag")
public class WmsCControlFlagController {
	@Autowired
	private WmsCControlFlagRemote wmsCControlFlagRemote;
	@Autowired
	private UserUtils userUtils;
	
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		 return wmsCControlFlagRemote.list(params);
	}
		
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		return wmsCControlFlagRemote.info(id);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCControlFlagEntity wmsCControlFlag) {
		wmsCControlFlag.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		wmsCControlFlag.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		wmsCControlFlag.setDel("0");
        return wmsCControlFlagRemote.save(wmsCControlFlag);
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCControlFlagEntity wmsCControlFlag) {
		wmsCControlFlag.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		wmsCControlFlag.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCControlFlagRemote.update(wmsCControlFlag);
	}
	
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCControlFlagEntity wmsCControlFlag = new WmsCControlFlagEntity();
		wmsCControlFlag.setId(id);
		wmsCControlFlag.setDel("X");
		return wmsCControlFlagRemote.delById(wmsCControlFlag);
	}
}

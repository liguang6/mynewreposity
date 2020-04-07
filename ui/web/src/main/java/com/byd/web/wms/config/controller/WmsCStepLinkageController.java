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
import com.byd.web.wms.config.entity.WmsCStepLinkageEntity;
import com.byd.web.wms.config.service.WmsCStepLinkageRemote;

/**
 * 
 * 一步联动主数据配置
 *
 */
@RestController
@RequestMapping("config/steplinkage")
public class WmsCStepLinkageController {
	@Autowired
private WmsCStepLinkageRemote wmsCStepLinkageRemote;
	@Autowired
    private UserUtils userUtils;
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		 return wmsCStepLinkageRemote.list(params);
	}
		
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		return wmsCStepLinkageRemote.info(id);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCStepLinkageEntity param) {
		param.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		param.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		param.setDel("0");
        return wmsCStepLinkageRemote.save(param);
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCStepLinkageEntity param) {
		param.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		param.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCStepLinkageRemote.update(param);
	}
	
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCStepLinkageEntity param = new WmsCStepLinkageEntity();
		param.setId(id);
		param.setDel("X");
		return wmsCStepLinkageRemote.delById(param);
	}
}

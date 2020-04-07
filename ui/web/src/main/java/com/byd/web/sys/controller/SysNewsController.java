package com.byd.web.sys.controller;

import java.util.Date;
import java.util.HashMap;
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
import com.byd.web.sys.service.SysNewsRemote;

/** 
 * 
 * @version 创建时间：2019年5月5日 上午8:42
 * 
 */
@RestController
@RequestMapping("/sys/news")
public class SysNewsController {
	@Autowired
	SysNewsRemote sysNewsRemote;
	@Autowired
	private UserUtils userUtils;
	
	
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		 return sysNewsRemote.list(params);
	}
	
	@RequestMapping("/query")
	public R query(@RequestParam Map<String, Object> params) {
		 return sysNewsRemote.query(params);
	}

	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody HashMap<String, Object> params) {
//		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//		Map<String,Object> params = (Map<String,Object>)jsonObject;
		params.put("Editor",userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		params.put("Edit_Date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return sysNewsRemote.save(params);
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return sysNewsRemote.info(id);
    }
    /**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody Map<String, Object> params) {
		params.put("Editor",userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		params.put("Edit_Date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return sysNewsRemote.update(params);
	}
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam(value="id") Long id){
		if(id == null){
			return R.error("参数错误");
		}
		return sysNewsRemote.delById(id);
	}
    
	/**
     * 根据账号、工厂等条件获取有效系统公告信息
     */
    @RequestMapping("/getUserNews")
    public R getUserNews(@RequestParam Map<String, Object> params){
		if(params == null){
			return R.error("参数错误");
		}
		return sysNewsRemote.getUserNews(params);
	}
}

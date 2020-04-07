package com.byd.admin.modules.sys.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.sys.service.SysNewsService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年5月5日 上午8:42
 * 类说明 
 */
@RestController
@RequestMapping("/sys/news")
public class SysNewsController {
	@Autowired
	private SysNewsService sysNewsService;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = sysNewsService.queryPage(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/query")
	public R query(@RequestParam Map<String, Object> params) {
		PageUtils page = sysNewsService.query(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/save")
	public R save(@RequestParam Map<String, Object> params) {
		sysNewsService.saveNoticeMail(params);
		return R.ok();
	}

	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		Map<String, Object> objectMap  = sysNewsService.selectById(id);
		return R.ok().put("objectMap",objectMap);
	}

	@RequestMapping("/update")
	public R update(@RequestParam Map<String, Object> params) {
		sysNewsService.updateById(params);
		return R.ok();
	}

	/**
	 * 单条记录删除
	 */
	@RequestMapping("/delById")
	public R delById(@RequestParam(value="id") Long id) {
		sysNewsService.delById(id);
		return R.ok();
	}
	
	/**
     * 根据账号、工厂等条件获取有效系统公告信息
     */
    @RequestMapping("/getUserNews")
    public R getUserNews(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> data = sysNewsService.getUserNews(params);
    	
    	return R.ok().put("data", data);
    }
}

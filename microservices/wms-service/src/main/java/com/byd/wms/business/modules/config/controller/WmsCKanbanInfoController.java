package com.byd.wms.business.modules.config.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.service.WmsCKanbanInfoService;




/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年5月5日 上午8:42
 * 类说明 
 */
@RestController
@RequestMapping("/config/KanbanInfo")
public class WmsCKanbanInfoController {
	@Autowired
	private WmsCKanbanInfoService wmsCKanbanInfoService;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wmsCKanbanInfoService.queryPage(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/query")
	public R query(@RequestParam Map<String, Object> params) {
		PageUtils page = wmsCKanbanInfoService.query(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/save")
	public R save(@RequestParam Map<String, Object> params) {
		wmsCKanbanInfoService.saveNoticeMail(params);
		return R.ok();
	}

	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		Map<String, Object> objectMap  = wmsCKanbanInfoService.selectById(id);
		return R.ok().put("objectMap",objectMap);
	}

	@RequestMapping("/update")
	public R update(@RequestParam Map<String, Object> params) {
		wmsCKanbanInfoService.updateById(params);
		return R.ok();
	}

	/**
	 * 单条记录删除
	 */
	@RequestMapping("/delById")
	public R delById(@RequestParam(value="id") Long id) {
		wmsCKanbanInfoService.delById(id);
		return R.ok();
	}
}

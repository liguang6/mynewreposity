package com.byd.wms.business.modules.config.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.service.WmsCNoticeMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
@RestController
@RequestMapping("config/noticemail")
public class WmsCNoticeMailController {
	@Autowired
	private WmsCNoticeMailService WmsCNoticeMailService;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = WmsCNoticeMailService.queryPage(params);
		return R.ok().put("page", page);
	}

	@RequestMapping("/save")
	public R save(@RequestParam Map<String, Object> params) {
		 WmsCNoticeMailService.saveNoticeMail(params);
		return R.ok();
	}

	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		Map<String, Object> objectMap  = WmsCNoticeMailService.selectById(id);
		return R.ok().put("objectMap",objectMap);
	}
	@RequestMapping("/update")
	public R update(@RequestParam Map<String, Object> params) {
		WmsCNoticeMailService.updateById(params);
		return R.ok();
	}

	/**
	 * 单条记录删除
	 */
	@RequestMapping("/delById")
	public R delById(@RequestParam(value="id") Long id) {
		WmsCNoticeMailService.delById(id);
		return R.ok();
	}
}

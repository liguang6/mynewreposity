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
import com.byd.web.wms.config.entity.WmsCMatReplaceEntity;
import com.byd.web.wms.config.service.WmsCMatReplaceRemote;

/**
 * 物料替代
 *
 */
@RestController
@RequestMapping("config/matreplace")
public class WmsCMatReplaceController {
	@Autowired
	private WmsCMatReplaceRemote wmsCMatReplaceRemote;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		return wmsCMatReplaceRemote.list(params);
	}

	/**
	 * 根据ID查询实体记录
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		return wmsCMatReplaceRemote.info(id);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCMatReplaceEntity wmsCMatReplace) {
		wmsCMatReplace.setCreator("");
		wmsCMatReplace.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		return wmsCMatReplaceRemote.save(wmsCMatReplace);
	}

	/**
	 * 单条记录删除(软删)
	 */
	@RequestMapping("/delById")
	public R delById(Long id) {
		if (id == null) {
			return R.error("参数错误");
		}
		WmsCMatReplaceEntity params = new WmsCMatReplaceEntity();
		params.setId(id);
		params.setDel("X");
		return wmsCMatReplaceRemote.delById(params);

	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCMatReplaceEntity wmsCMatReplace) {
		wmsCMatReplace.setEditor("");
		wmsCMatReplace.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		return wmsCMatReplaceRemote.update(wmsCMatReplace);
	}

}

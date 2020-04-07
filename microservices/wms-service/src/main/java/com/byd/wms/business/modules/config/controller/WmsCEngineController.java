package com.byd.wms.business.modules.config.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.dao.WmsCEngineDao;
import com.byd.wms.business.modules.config.entity.WmsCControlFlagEntity;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCControlFlagService;
import com.byd.wms.business.modules.config.service.WmsCEngineService;

/**
 * 
 * 发动机仓库物料日报表配置
 *
 */
@RestController
@RequestMapping("config/engine")
public class WmsCEngineController {

	@Autowired
	private WmsCEngineService wmsCEngineService;

	@Autowired
	private WmsCEngineDao wmsCEngineDao;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {

		PageUtils page = wmsCEngineService.queryPage(params);
		return R.ok().put("page", page);
	}

	@RequestMapping("/info")
	public R info(@RequestParam Long HEADID, @RequestParam Long ITEMID) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("HEADID", HEADID);
		map.put("ITEMID", ITEMID);
		Map<String, Object> result = wmsCEngineService.selectById(map);
		return R.ok().put("result", result);
	}

	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody Map<String, Object> params) {

		List<Map<String, Object>> list = wmsCEngineDao.selectAll(params);
		List<Map<String, Object>> list1 = wmsCEngineDao.selectItemAll(params);
		if (list.size() > 0 && list1.size() > 0) 
			return R.error("信息已维护！");
		if (list.size() == 0 && list1.size() == 0)
			wmsCEngineService.insert(params);
		if (list.size() == 0 && list1.size() > 0)	
			wmsCEngineDao.insert(params);
		if (list.size() > 0 && list1.size() == 0)	
			wmsCEngineDao.insertItem(params);
		return R.ok();
	}

	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody Map<String, Object> params) {

		wmsCEngineService.updateById(params);
		return R.ok();
	}

	@RequestMapping("/delete")
	public R delete(@RequestBody Map<String, Object> params) {
		wmsCEngineService.deleteById(params);
		return R.ok();
	}

}

package com.byd.wms.business.modules.kn.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.kn.service.WmsCBarcodeSterilisationService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("kn/barcodeSterilisation")
public class WmsCBarcodeSterilisationController {
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsCBarcodeSterilisationService WmsCBarcodeSterilisationService;
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = WmsCBarcodeSterilisationService.queryPage(params);
		return R.ok().put("page", page);
	}
		
	@RequestMapping("/one")
	public R info(@RequestParam Map<String, Object> params) {
		PageUtils page = WmsCBarcodeSterilisationService.queryOne(params);
		return R.ok().put("page", page);
	}

	@RequestMapping("/saveCoreLabel")
	public R save(@RequestParam Map<String, Object> params) {
		PageUtils page =WmsCBarcodeSterilisationService.saveCoreLabel(params);
		return R.ok().put("page", page);
	}

//	@RequestMapping("/labelLabelPreview")
//	public R print(@RequestParam Map<String, Object> params) {
//		PageUtils page =WmsCBarcodeSterilisationService.printCoreLabel(params);
//		return R.ok().put("page", page);
//	}
}

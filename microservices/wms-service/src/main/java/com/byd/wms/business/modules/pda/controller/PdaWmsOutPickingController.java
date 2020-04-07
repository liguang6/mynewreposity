package com.byd.wms.business.modules.pda.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.out.service.WmsOutPickingService;
import com.byd.wms.business.modules.pda.service.PdaWmsOutPickingService;


/**
 * pda需求拣配
 * 
 *
 *
 */
@RestController
@RequestMapping("/out/picking")
public class PdaWmsOutPickingController {

	@Autowired
	private PdaWmsOutPickingService pdaWmsOutPickingService;
	
	@Autowired
	private WmsOutPickingService wmsOutPickingService;
	
	@Autowired
	private WmsCDocNoService wmsCDocNoService;

	/**
	 * PDA推荐储位
	 */
	@CrossOrigin
	@RequestMapping("/pdaRecommend1")
	public R pdaRecommend1(@RequestParam Map<String, Object> params) {
		return R.ok().put("list", pdaWmsOutPickingService.pdaRecommend(params));
	}
	
	@CrossOrigin
    @RequestMapping("/pdaRecommend")
    public R pdaRecommend(@RequestParam Map<String, Object> params){
        PageUtils page = wmsOutPickingService.recommend(params);
        return R.ok().put("page", page);
    }
    
	/**
	 * PDA下架
	 */
	@CrossOrigin
	@RequestMapping("/pdaPicking")
	public R pdaPicking(@RequestBody Map<String, Object> params) {
		try {
			pdaWmsOutPickingService.pdaPicking(params);
			return R.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return R.error();
		}

	}
	
	@CrossOrigin
	@RequestMapping("/getLabel")
	public R getLabel(@RequestBody Map<String, Object> params) {
		return R.ok().put("labelNo", pdaWmsOutPickingService.getLabel(params));
	}
}

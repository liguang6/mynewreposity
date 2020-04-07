package com.byd.wms.business.modules.config.controller;


import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.service.WmsCShippingListService;

/**
 *
 * @author 
 * @email 
 * @date 
 */
@RestController
@RequestMapping("config/shipping")
public class WmsCShippingListController {
    @Autowired
    private WmsCShippingListService wmsCShippingListService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCShippingListService.queryPage(params);
        return R.ok().put("page", page);
    }

	@RequestMapping("/save")
	public R save(@RequestParam Map<String, Object> params) {
		wmsCShippingListService.saveNoticeMail(params);
		return R.ok();
	}

	@RequestMapping("/info/{id}/{itemNo}")
	public R info(@PathVariable("id") String id,@PathVariable("itemNo") String itemNo){
		Map<String, Object> objectMap  = wmsCShippingListService.selectById(id,itemNo);
		return R.ok().put("objectMap",objectMap);
	}

	@RequestMapping("/update")
	public R update(@RequestParam Map<String, Object> params) {
		wmsCShippingListService.updateById(params);
		return R.ok();
	}


}

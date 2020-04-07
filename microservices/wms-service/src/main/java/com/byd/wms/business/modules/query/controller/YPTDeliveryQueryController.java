package com.byd.wms.business.modules.query.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.WmsInReceiptQueryService;
import com.byd.wms.business.modules.query.service.YPTDeliveryQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;



/**
 * @author rain
 * @date 2019年11月20日16:51:03
 * @description 云平台送货单查询控制类
 */
@RestController
@RequestMapping("query/yptquery")
public class YPTDeliveryQueryController {
	@Autowired
    private YPTDeliveryQueryService yptDeliveryQueryService;
	@Autowired
	WmsInReceiptQueryService wmsInReceiptQueryService;

	/**
	 * @description 列表页面查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = yptDeliveryQueryService.queryPage(params);
        return R.ok().put("page", page);
	}


	/**
	 * @description 明细页面查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/listitem")
	public R listitem(@RequestParam Map<String, Object> params){
		PageUtils page = yptDeliveryQueryService.queryPageDetail(params);
		return R.ok().put("page", page);
	}


	/**
	 * 云平台送货单条码明细查询
	 * @date 2019年11月25日17:13:04
	 * @param params
	 * @return
	 */
	@RequestMapping("/yptdeliverbarlist")
	public R yptdeliverbarlist(@RequestParam Map<String, Object> params){
		PageUtils page = yptDeliveryQueryService.queryBarPage(params);
		return R.ok().put("page", page);
	}

}

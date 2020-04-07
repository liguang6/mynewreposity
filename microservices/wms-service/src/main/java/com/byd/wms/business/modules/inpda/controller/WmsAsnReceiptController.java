package com.byd.wms.business.modules.inpda.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.wms.business.modules.in.service.SCMDeliveryService;
import com.byd.wms.business.modules.inpda.service.WmsAsnReceiptService;

/**
 * PDA送货单收货
 * @author ren.wei3
 *
 */

@RestController
@RequestMapping("/in/asnReceipt")
public class WmsAsnReceiptController {

	@Autowired
	private WmsAsnReceiptService wmsAsnReceiptService;
	
	@RequestMapping("/scan")
    public R scan(@RequestBody Map<String, Object> params){
		try{
			Map<String, Object> retMap = wmsAsnReceiptService.scan(params);
			
			return R.ok().put("result", retMap.get("result")).put("boxs", retMap.get("boxs"));
		} catch (Exception e) {
			return R.error(e.getMessage());	
		}	
    }
	
}

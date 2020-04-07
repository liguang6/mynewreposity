package com.byd.wms.business.modules.inpda.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.inpda.service.WmsAutoPutawayPdaService;

/**
 * PDA一步联动收货
 * @author ren.wei3
 *
 */
@RestController
@RequestMapping("/in/autoPutaway")
public class WmsAutoPutawayPdaController {
	
	@Autowired
	private WmsAutoPutawayPdaService wmsAutoPutawayPdaService;
	

	@RequestMapping("/scan")
    public R scan(@RequestBody Map<String, Object> params){
		try{
			Map<String, Object> retMap = wmsAutoPutawayPdaService.scan(params);
			return R.ok().put("result", retMap.get("result")).put("boxs", retMap.get("boxs"));
		} catch (Exception e) {
			return R.error(e.getMessage());	
		}	
    }
	
	@RequestMapping("/docItem")
    public R docItem(@RequestBody  Map<String,Object> params) {
		try {
			PageUtils page = wmsAutoPutawayPdaService.docItem(params);
			return R.ok().put("page", page);
		} catch(Exception e) {
    		return R.error(e.getMessage());
    	}
	}
	
	@RequestMapping("/labelItem")
	public R labelItem(@RequestBody Map<String, Object> params) {
		
		PageUtils page = wmsAutoPutawayPdaService.labelItem(params);
        return R.ok().put("page", page);
	}
	
	@RequestMapping("/deleteLabel")
	public R deleteLabel(@RequestBody Map<String, Object> params) {
		try {
			wmsAutoPutawayPdaService.deleteLabel(params);
		} catch(Exception e) {
    		return R.error(e.getMessage());
    	}
        return R.ok();
	}
	
	/**
     * 过账
     * @param params
     * @return
     */
    @RequestMapping("/confirm")
    public R confirm(@RequestBody  Map<String,Object> params) {
    	try {
    		Map<String, Object> retMap = wmsAutoPutawayPdaService.confirm(params);
    		String receiptno = retMap.get("receiptno").toString();
    		String iqcno = retMap.get("iqcno").toString();
    		String wmsnostr = retMap.get("wmsnostr").toString();
    		String sapdoc = retMap.get("sapdoc").toString();
    		String operationTime = retMap.get("operationTime").toString();
			return R.ok().put("receiptno", receiptno)
					.put("iqcno", iqcno)
					.put("wmsno", wmsnostr)
					.put("sapdoc", sapdoc)
					.put("operationTime", operationTime);
		} catch(Exception e) {
    		return R.error(e.getMessage());
    	}
        
    }
}

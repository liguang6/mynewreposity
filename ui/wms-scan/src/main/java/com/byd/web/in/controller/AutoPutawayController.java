package com.byd.web.in.controller;

import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.in.service.WmsAutoPutawayPdaRemote;

/**
 * PDA一步联动收货
 * @author ren.wei3
 *
 */

@RestController
@RequestMapping("/in/autoPutaway")
public class AutoPutawayController {

	@Autowired 
	protected HttpSession session;
	@Autowired
    private UserUtils userUtils;
	@Autowired
    private WmsAutoPutawayPdaRemote wmsAutoPutawayPdaRemote;
	
	/**
	 * 扫描
	 * @param params
	 * @return
	 */
	@RequestMapping("/scan")
    public R scan(@RequestParam Map<String, Object> params){
		params.put("WH_NUMBER", session.getAttribute("warehouse"));
		params.put("WERKS", session.getAttribute("werks"));
		params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
    	params.put("MENU_KEY", "PDA_GR_01");
		
    	return wmsAutoPutawayPdaRemote.scan(params);
    }
	
	/**
	 * 凭证明细
	 * @param params
	 * @return
	 */
    @RequestMapping("/docItem")
    public R docItem(@RequestParam  Map<String,Object> params) {
    	params.put("WH_NUMBER", session.getAttribute("warehouse"));
    	params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
    	params.put("MENU_KEY", "PDA_GR_01");
    	
    	return wmsAutoPutawayPdaRemote.docItem(params);
	}
    
    /**
     * 条码明细
     * @param params
     * @return
     */
    @RequestMapping("/labelItem")
    public R labelItem(@RequestParam  Map<String,Object> params) {
    	params.put("WH_NUMBER", session.getAttribute("warehouse"));
    	params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
    	params.put("MENU_KEY", "PDA_GR_01");
    	params.put("REF_DOC_NO", params.get("docNo"));
		params.put("REF_DOC_ITEM", params.get("docItem"));
    	
    	return wmsAutoPutawayPdaRemote.labelItem(params);
	}
    
    /**
     * 删除条码缓存表
     * @param params
     * @return
     */
    @RequestMapping("/deleteLabel")
    public R deleteLabel(@RequestParam  Map<String,Object> params) {
    	params.put("WH_NUMBER", session.getAttribute("warehouse"));
    	params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
    	params.put("MENU_KEY", "PDA_GR_01");
    	
    	return wmsAutoPutawayPdaRemote.deleteLabel(params);
	}
    
    /**
     * 过账
     * @param params
     * @return
     */
    @RequestMapping("/confirm")
    public R confirm(@RequestParam  Map<String,Object> params) {
    	params.put("WH_NUMBER", session.getAttribute("warehouse"));
    	params.put("WERKS", session.getAttribute("werks"));
    	params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
    	params.put("MENU_KEY", "PDA_GR_01");
    	params.put("HEADER_TXT",params.get("headertxt"));
    	params.put("PZDDT",params.get("docDate"));
    	params.put("JZDDT",params.get("debitDate"));
    	
    	return wmsAutoPutawayPdaRemote.confirm(params);
	}
}

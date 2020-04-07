package com.byd.web.in.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.in.service.WmsAsnReceiptRemote;

/**
 * PDA送货单收货
 * @author ren.wei3
 *
 */

@RestController
@RequestMapping("/in/asnReceipt")
public class AsnReceiptController {

	@Autowired 
	protected HttpSession session;
	@Autowired
    private UserUtils userUtils;
	@Autowired
	private WmsAsnReceiptRemote wmsAsnReceiptRemote;
	
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
    	params.put("MENU_KEY", "PDA_GR_04");
		
    	return wmsAsnReceiptRemote.scan(params);
    }
}

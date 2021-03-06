package com.byd.web.kn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.kn.service.WmsKnStockTransferAPdaRemote;
import com.byd.web.wms.common.service.CommonRemote;

/*411K
 * @author chen.yafei
 * @email chen.yafei1@byd.com
 * @date 2019-12-11
 */
@RestController
@RequestMapping("kn/stockTransferA")
public class WmsKnStockTransferAPdaController {
	
	@Autowired 
	protected HttpSession session;
	@Autowired
    private UserUtils userUtils;
	@Autowired
	private WmsKnStockTransferAPdaRemote wmsKnStockTransferAPdaRemote;
	@Autowired
	private CommonRemote commonRemote;
	
	private final String MENU_KEY="PDA_KN_01";
	//获取标签信息
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		params.put("MENU_KEY", MENU_KEY);
		String wh_number=(String) session.getAttribute("warehouse");
		String werks=(String) session.getAttribute("werks");
		params.put("WERKS", werks);
		params.put("WH_NUMBER", wh_number);
		params.put("LABEL_STATUS", "08");
		params.put("SOBKZ", "K");
		R r=wmsKnStockTransferAPdaRemote.list(params);
		
		return r;
	} 
	//获取标签信息
	@RequestMapping("/labelList")
	public R labelList(@RequestParam Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		params.put("MENU_KEY", MENU_KEY);
		String wh_number=(String) session.getAttribute("warehouse");
		String werks=(String) session.getAttribute("werks");
		params.put("WERKS", werks);
		params.put("WH_NUMBER", wh_number);
		params.put("LABEL_STATUS", "08");
		params.put("SOBKZ", "K");
		String label=params.get("LABEL").toString();
		String[] labelList=label.trim().split(",");
		params.put("LABEL_NO_LIST", labelList);
		R r=wmsKnStockTransferAPdaRemote.labelList(params);
		
		return r;
	} 
	@RequestMapping("/deleteLabel")
    public R deleteLabel(@RequestParam  Map<String,Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
    	params.put("WH_NUMBER", session.getAttribute("warehouse"));
    	params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    	params.put("MENU_KEY", MENU_KEY);
    	
    	return wmsKnStockTransferAPdaRemote.deleteLabel(params);
	}
	
	@RequestMapping("/save")
	public R save(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		params.put("MENU_KEY", MENU_KEY);
		String wh_number=(String) session.getAttribute("warehouse");
		String werks=(String) session.getAttribute("werks");
		params.put("WERKS", werks);
		params.put("WH_NUMBER", wh_number);
		params.put("LABEL_STATUS", "08");
		params.put("SOBKZ", "K");		
		params.put("WMS_MOVE_TYPE", "411_K");
		
		params.put("RECEIVER", "");
		params.put("BIN_CODE", "");
		params.put("MATNR_T", "");
		params.put("BATCH", "");
		params.put("LIFNR", "");
		params.put("SO_NO", "");
		params.put("SO_ITEM_NO", "");
		
		
		R r=wmsKnStockTransferAPdaRemote.save(params);
		return r;
	}
	
	@RequestMapping("/getLoList")
	public R getLoList(@RequestParam Map<String, Object> params){
		String werks=(String) session.getAttribute("werks");
		params.put("WERKS", werks);
		params.put("SOBKZ", "Z");	
		return commonRemote.getLoList(params);	
	}
	
	

}

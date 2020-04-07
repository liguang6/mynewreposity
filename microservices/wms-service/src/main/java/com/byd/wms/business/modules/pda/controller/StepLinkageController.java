package com.byd.wms.business.modules.pda.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.in.service.WmsInAutoPutawayService;



/**
  * PDA一步联动过账
 *
 */
@RestController
@RequestMapping("/stepLinkage")
public class StepLinkageController {

	@Autowired
    private UserUtils userUtils;
	
	@Autowired
	private WmsInAutoPutawayService wmsInAutoPutawayService;
	
	@CrossOrigin
	@RequestMapping("/handover")
	public R handover(@RequestBody Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME"));
	   	params.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
	   	params.put("FROMWERKS", params.get("WERKS_FROM"));
	   	params.put("WERKS", params.get("WERKS_TO"));
	   	params.put("PZDDT", params.get("PZ_DATE"));
	   	params.put("JZDDT", params.get("JZ_DATE"));
	   	params.put("HEADER_TXT", params.get("HEADTXT"));
	   	
		Map<String, Object> retMap=new HashMap<String, Object>();
		String wmsno = "";
		String receiptno = "";
		String iqcno = "";
		String sapdoc = "";

		try{
			
			List<Map<String,String>> itemList = (List<Map<String,String>>) JSONArray.parse(params.get("ITEMLIST").toString());
			int itemNo = 10;
			for (Map<String,String> item : itemList) {
				item.put("ITEM_NO", String.format("%5d", itemNo).replace(" ", "0"));
				item.put("RECEIVE_QTY", String.valueOf(item.get("QTY")));
				item.put("BIN_CODE", params.get("BINCODE") == null ? "":params.get("BINCODE").toString());
				itemNo = itemNo + 10;
			}
			params.put("ITEMLIST", JSONArray.toJSONString(itemList));
			//创建PO
			retMap = wmsInAutoPutawayService.createPO(params);
			
			String poNumber = retMap.get("PO_NUMBER") == null ? "":retMap.get("PO_NUMBER").toString();
			String message = retMap.get("MESSAGE") == null ? "":retMap.get("MESSAGE").toString();
			//保存日志和状态
			params.put("PO_NUMBER", poNumber);
			params.put("MESSAGE", message);
			String timestampstr = wmsInAutoPutawayService.saveStepLog(params);
			
			//创建DN
			if(!poNumber.equals("")) {
				Thread.sleep(1000);
				params.put("EBELN", poNumber);
				params.put("TIME_STAMP_STR", timestampstr);
				retMap = wmsInAutoPutawayService.createDN(params);
			}
			String delivery = retMap.get("DELIVERY") == null ? "":retMap.get("DELIVERY").toString();
			
			//交货单收货
			if(!delivery.equals("")) {
				Thread.sleep(1000);
				retMap = wmsInAutoPutawayService.postDN(params);
				
				wmsno = retMap.get("WMS_NO") == null ? "":retMap.get("WMS_NO").toString();
				receiptno = retMap.get("RECEIPT_NO") == null ? "":retMap.get("RECEIPT_NO").toString();
				iqcno = retMap.get("INSPECTION_NO") == null ? "":retMap.get("INSPECTION_NO").toString();
				sapdoc = retMap.get("MAT_DOC") == null ? "":retMap.get("MAT_DOC").toString();
				String message1 = retMap.get("MESSAGE") == null ? "":retMap.get("MESSAGE").toString();
				
				//更新日志状态
				List<Map<String,String>> statusList = new ArrayList<Map<String,String>>();
				Map<String, String> statusparam = new HashMap<String, String>();
				if (wmsno.equals("")) {
					statusparam.put("STEP3_STATUS", "E");
					statusparam.put("MSG3", message1.substring(0, message1.length()>300?300:message1.length()));
				} else {
					statusparam.put("STEP3_STATUS", "S");
					statusparam.put("RECEIPT_NO", receiptno);
					statusparam.put("INSPECTION_NO", iqcno);
					statusparam.put("WMS_NO", wmsno);
					statusparam.put("MAT_DOC", sapdoc);
					statusparam.put("MSG3", " ");
				}
				statusparam.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				statusparam.put("WH_NUMBER", params.get("WH_NUMBER").toString());
				statusparam.put("TIME_STAMP_STR", params.get("TIME_STAMP_STR").toString());
				statusList.add(statusparam);
				wmsInAutoPutawayService.updatePutawayStatus(statusList);
			}
			
			
			if (!message.equals("")) {
				return R.error("收货失败！原因：" + message);
			}
			
		} catch (Exception e) {
			return R.error("系统异常："+e.getMessage());	
		}	
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("wmsno", wmsno);
		map.put("receiptno", receiptno);
		map.put("iqcno", iqcno);
		map.put("sapdoc", sapdoc);
		
		return R.ok().put("data", map);
	}

}

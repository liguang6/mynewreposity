package com.byd.sap.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.byd.sap.service.IwmsSapRealtimeService;
import com.byd.sap.service.IwmsSapService;

@RestController
@RequestMapping("/hello")
public class HelloController {
	@Autowired
	private IwmsSapService wmsSapService;
	
	@Autowired
	private IwmsSapRealtimeService wmsSapRealService;
	
	@PostMapping("/hello")
	@ResponseBody
    public String hello(@RequestParam String name) {
        return "Hello, " + name + " " + new Date();
    }
	
	@RequestMapping("/test")
	@ResponseBody
    public String test() {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("WERKS", "C161");
		
		paramMap.put("VBELN_VL", "0800028183");		//VBELN_VL-交货单号（10）
		paramMap.put("VBELN", "0800028183");		//VBELN-提货单号（10） 可填
		paramMap.put("KOMUE", "X");					//KOMUE-自动以拣配数量覆盖交货数量（1） 可填
		paramMap.put("WABUC", "X");					//WABUC-自动过帐货物移动（1） 可填
		paramMap.put("WADAT_IST", "20180731");		//WADAT_IST-实际货物移动日期（8） 必填
		paramMap.put("COMMIT ", "X");				//事务提交 字符串 必填
		paramMap.put("DELIVERY ", "800028183");		//交货单号 字符串 必填
		paramMap.put("UPDATE_PICKING ", "X");		//更新简配 字符串 过账时必填，修改交货单不填
		paramMap.put("IF_ERROR_MESSAGES_SEND_0 ", "X");			//错误消息返回  字符串 必填
		
		List<Map<String,String>> itemList = new ArrayList<Map<String,String>>();
		Map<String,String> item = new HashMap<String,String>();
		item.put("VBELN_VL", "0800028183");			//VBELN_VL-交货单号（10） 必填
		item.put("POSNR_VL", "10");					//POSNR_VL-交货行项目号（4）必填
		item.put("VBELN", "0800028183");			//VBELN-后续凭证编号（6） 默认和交货单号一致 可填
		item.put("POSNN", "10");					//POSNN-后续凭证行项目号（6） 可填
		item.put("MATNR", "10878210-00");			//MATNR-物料号（18） 必填	
		item.put("WERKS", "C190");					//WERKS-工厂（4） 必填
		item.put("LIANP", "X");						//LIANP-修改交货数量（1）
		item.put("LIPS_DEL", "X");					//LIPS_DEL-标志：删除交货项（1）
		item.put("LGMNG", "1");						//LGMNG-实际交货数量（13） 必填
		item.put("LGORT", "0030");					//LGORT-库存地点（4） 必填
		
		itemList.add(item);
		paramMap.put("VBPOK_TAB", itemList);
		
		Map<String,Object> result = wmsSapRealService.sapDeliveryUpdate(paramMap);
        return "Hello:test, result : " + result ;
    }
}

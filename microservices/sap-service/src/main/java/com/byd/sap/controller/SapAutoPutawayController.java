package com.byd.sap.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.byd.sap.service.IWmsAutoPutawayService;

/**
 * STO一步联动收货
 * @author ren.wei3
 *
 */
@RestController
public class SapAutoPutawayController {
	
	@Autowired
	private IWmsAutoPutawayService wmsAutoPutawayService;
	
	/**
	 * 创建PO
	 * @param params
	 * @return
	 */
	@PostMapping("/createStoPO")
	@ResponseBody
	public Map<String,Object> createStoPO(@RequestBody Map<String,Object> params){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try{
			returnMap = wmsAutoPutawayService.createStoPO(params);
		} catch (Exception e) {
			returnMap.put("CODE", "-10");
            returnMap.put("MESSAGE", "系统异常："+e.getMessage());
		}
		return returnMap;
	}
	
	/**
	 * 创建交货单
	 * @param params
	 * @return
	 */
	@PostMapping("/createStoDN")
	@ResponseBody
	public Map<String,Object> createStoDN(@RequestBody Map<String,Object> params){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try{
			returnMap = wmsAutoPutawayService.createStoDN(params);
		} catch (Exception e) {
			returnMap.put("CODE", "-10");
            returnMap.put("MESSAGE", "系统异常："+e.getMessage());
		}
		return returnMap;
	}
	
	/**
	 * 交货单过账
	 * @param werks
	 * @param vbeln
	 * @return
	 */
	@PostMapping("/postDN")
	@ResponseBody
	public Map<String,Object> postDN(@RequestParam String werks, @RequestParam String vbeln){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try{
			returnMap = wmsAutoPutawayService.postDN(werks,vbeln);
		} catch (Exception e) {
			returnMap.put("CODE", "-10");
            returnMap.put("MESSAGE", "系统异常："+e.getMessage());
		}
		return returnMap;
	}
}

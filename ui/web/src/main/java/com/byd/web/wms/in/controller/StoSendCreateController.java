package com.byd.web.wms.in.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.wms.in.service.StoSendCreateRemote;

/**
 * 
 sto送货单创建、查询
 */
@Controller
@RequestMapping("in/sto")
public class StoSendCreateController {

	@Autowired 
	StoSendCreateRemote stoSendCreateRemote;
	
	@RequestMapping("/list")
	@ResponseBody
	public R list(@RequestParam Map<String, Object> params){
		return stoSendCreateRemote.list(params);
	}
	
	@RequestMapping("/create")
	@ResponseBody
	public R create(@RequestParam Map<String, Object> params){
		return  stoSendCreateRemote.create(params);
	}
	
	@RequestMapping("/to_stoList")
	public String toStoList(String stoNo,Model model){
		
		model.addAttribute("stoNo", stoNo);
		return "wms/in/wms_sto_query";
	}
	
	@RequestMapping("/querySto")
	@ResponseBody
	public R querySto(@RequestParam Map<String, Object> params){
		return stoSendCreateRemote.querySto(params);
	}
	
	@RequestMapping("/queryContact")
	@ResponseBody
	public R queryContact(@RequestParam Map<String, Object> params){
		return stoSendCreateRemote.queryContact(params);
	}
	
	@RequestMapping("/queryCustomer")
	@ResponseBody
	public R queryCustomer(@RequestParam Map<String, Object> params){
		return stoSendCreateRemote.queryCustomer(params);
	}
	
	@RequestMapping("/queryWMSNo")
	@ResponseBody
	public R queryWMSNo(@RequestParam Map<String, Object> params){
		return stoSendCreateRemote.queryWMSNo(params);
	}
	
	@RequestMapping("/queryLiktx")
	@ResponseBody
	public R queryLiktx(@RequestParam Map<String, Object>  params){
		System.err.print(params);
		return stoSendCreateRemote.queryLiktx(params);
	}
	
	
	@RequestMapping("/checkAddr")
	@ResponseBody
	public R checkAddr(@RequestParam Map<String, Object> params){
		return stoSendCreateRemote.checkAddr(params);
	}
	
	@RequestMapping("/checkExist")
	@ResponseBody
	public R checkExist(@RequestParam Map<String, Object> params){
		return stoSendCreateRemote.checkExist(params);
	}
	
	@RequestMapping("/queryBydeliveryNo")
	@ResponseBody
	public R queryBydeliveryNo(@RequestBody List<Map<String, Object>> params){
		return  stoSendCreateRemote.queryBydeliveryNo(params);

	}
	
}

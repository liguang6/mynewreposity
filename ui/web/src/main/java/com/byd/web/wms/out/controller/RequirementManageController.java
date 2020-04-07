package com.byd.web.wms.out.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.byd.utils.R;
import com.byd.web.wms.out.service.WmsOutReqServiceRemote;

/**
 * 需求查询，删除，关闭
 * @author yang lin
 * @date 2019-06-03
 *
 */
@Controller
@RequestMapping("/out/requirement")
public class RequirementManageController {
	@Autowired
	WmsOutReqServiceRemote service;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//出库需求查询
	@RequestMapping("/list")
	@ResponseBody
	public R requirementList(@RequestParam Map<String,Object> params) {
		transfer(params);
		return service.queryRequirementList(params);
	}
	

	@RequestMapping("/info/{reqNo}/{werks}/{whNumber}/{status}")
	public String requirementItem(@PathVariable("reqNo") String reqNo,@PathVariable("werks")String werks,
								  @PathVariable("whNumber")String whNumber,
								  @PathVariable("status")String status, Model model) {
		model.addAttribute("REQUIREMENT_NO", reqNo);
		model.addAttribute("WERK", werks);
		model.addAttribute("WHNUM", whNumber);
		model.addAttribute("REQUIREMENT_STATUS", status);
		return "wms/out/requirement_manage_iteminfo";
	}

	/**
	 * 需求删除
	 * @param params
	 * @return
	 */
	@RequestMapping("/close")
	@ResponseBody
	public R closeRequirement(@RequestParam Map<String,Object> params) {
		System.err.println(params.get("params").toString());
		return service.closeRequirement(params);
	}
	
	//出库需求明细查
	@RequestMapping("/items")
	@ResponseBody
	public R requiremnetItemsList(@RequestParam Map<String,Object> params) {
		transfer(params);
		return service.queryRequirementItemsList(params);
	}

	//参数转换
	void transfer(Map<String,Object> params) {
		String MATNR_LIST = (String) params.get("MATNR_LIST");
		String REQUIRED_DATE = (String) params.get("REQUIRED_DATE");
		String CREATE_DATE = (String) params.get("CREATE_DATE");
		
		//料号字符转换成数组
		List<String> MATNR = new ArrayList<String>();
		if(MATNR_LIST != null && MATNR_LIST.trim().length() > 0) {
			String[] matnrs = MATNR_LIST.split(",");
			for(String matnr:matnrs) {
				MATNR.add(matnr);
			}
		}
		params.put("MATNR", MATNR);
		
		//需求日期范围字符。解析成对象
		List<Map<String,Object>> REQUIRED_DATE_LIST = new ArrayList<Map<String,Object>>();
		if(REQUIRED_DATE != null && REQUIRED_DATE.trim().length() > 0) {
			String[] requireDateRangeArray = REQUIRED_DATE.split(",");
			for(String reqDateRange:requireDateRangeArray) {
				String[] rang = reqDateRange.split("~");
				if(rang.length == 1) {
					Map<String,Object> temp = new HashMap<String, Object>();
					temp.put("DATE", rang[0]);
					REQUIRED_DATE_LIST.add(temp);
				}
				if(rang.length != 2) {//不合法的范围，略过
					continue;
				}
				try {
					sdf.parse(rang[0]);
					sdf.parse(rang[1]);
					Map<String,Object> temp = new HashMap<String, Object>();
					temp.put("START", rang[0].trim());
					temp.put("END", rang[1].trim());
					REQUIRED_DATE_LIST.add(temp);
				} catch (ParseException e) {
					//不合格的日期格式略过
				}
			}
		}
		params.put("REQUIRED_DATE_LIST", REQUIRED_DATE_LIST);
		
		
		// 需求日期范围字符。解析成对象
		List<Map<String, Object>> CREATE_DATE_LIST = new ArrayList<Map<String, Object>>();
		if (CREATE_DATE != null && CREATE_DATE.trim().length() > 0) {
			String[] createDateRangeArray = CREATE_DATE.split(",");
			for (String creDateRange : createDateRangeArray) {
				String[] rang = creDateRange.split("~");
				if (rang.length == 1) {
					Map<String, Object> temp = new HashMap<String, Object>();
					temp.put("DATE", rang[0]);
					CREATE_DATE_LIST.add(temp);
				}
				if (rang.length != 2) {// 不合法的范围，略过
					continue;
				}

				try {
					sdf.parse(rang[0]);
					sdf.parse(rang[1]);
					Map<String, Object> temp = new HashMap<String, Object>();
					temp.put("START", rang[0].trim());
					temp.put("END", rang[1].trim());
					CREATE_DATE_LIST.add(temp);
				} catch (ParseException e) {
					// 不合格的日期格式略过
				}
			}
		}
		params.put("CREATE_DATE_LIST", CREATE_DATE_LIST);
	}
	
}

package com.byd.wms.business.modules.out.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.service.RequirementManageService;

@Controller
@RequestMapping("/out/requirement")
public class RequirementManageController {
	
	@Autowired
	RequirementManageService reqManageService;
	
	@Autowired
	WmsOutRequirementItemDao reqItemDAO;
	

	@RequestMapping("/list")
	@ResponseBody
	public R listRequirement(@RequestBody Map<String,Object> params) {
		Integer currentPage = Integer.parseInt(params.get("pageNo").toString());
		Integer pageSize = Integer.parseInt(params.get("pageSize").toString());
		PageUtils page =  reqManageService.selectRequirementHeadList(params, currentPage, pageSize);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/items")
	@ResponseBody
	public R requiremntItemList(@RequestBody Map<String,Object> params) {
		
 		Integer currentPage = Integer.parseInt(params.get("pageNo").toString());
		Integer pageSize = Integer.MAX_VALUE;//Integer.parseInt(params.get("pageSize").toString());
		PageUtils page = reqManageService.selectRequirementItemList(params, currentPage, pageSize);
		return R.ok().put("page", page);
	}

	/**
	 * 需求的状态关闭/删除
	 * @param params
	 * @return
	 */
	@RequestMapping("/close")
	@ResponseBody
	public R deleteAndClose(@RequestBody Map<String,Object> params) {
		System.err.println("params "+params.toString());
		return reqManageService.closeRrequirement(params);
	}
}

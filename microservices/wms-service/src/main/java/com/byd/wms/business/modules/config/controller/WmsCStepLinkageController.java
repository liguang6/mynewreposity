package com.byd.wms.business.modules.config.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.StringUtils;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleEntity;
import com.byd.wms.business.modules.config.entity.WmsCStepLinkageEntity;
import com.byd.wms.business.modules.config.service.WmsCStepLinkageService;

/**
 * 
 * 一步联动主数据配置
 *
 */
@RestController
@RequestMapping("config/steplinkage")
public class WmsCStepLinkageController {
	@Autowired
	private WmsCStepLinkageService wmsCStepLinkageService;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wmsCStepLinkageService.queryPage(params);
		return R.ok().put("page", page);
	}
		
	@RequestMapping("/info")
	public R info(@RequestParam Long id) {
		WmsCStepLinkageEntity param = wmsCStepLinkageService.selectById(id);
		return R.ok().put("wmsCStepLinkage", param);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCStepLinkageEntity param) {
		List<WmsCStepLinkageEntity> list=wmsCStepLinkageService.selectList(
	    		 new EntityWrapper<WmsCStepLinkageEntity>()
	    		 .eq(StringUtils.isNotBlank(param.getWarehouseCode()),"WH_NUMBER", param.getWarehouseCode()).eq(StringUtils.isNotBlank(param.getWerksFrom()),"WERKS_FROM", param.getWerksFrom())
	    		 .eq(StringUtils.isNotBlank(param.getWerksTo()),"WERKS_TO", param.getWerksTo()).eq(StringUtils.isNotBlank(param.getEkgrp()),"EKGRP", param.getEkgrp())
	    		 .eq(StringUtils.isNotBlank(param.getDocType()),"DOC_TYPE", param.getDocType()).eq(StringUtils.isNotBlank(param.getTaxCode()),"TAX_CODE", param.getTaxCode())
	    		 .eq(StringUtils.isNotBlank(param.getBuName()),"BU_NAME", param.getBuName()).eq(StringUtils.isNotBlank(param.getEkorg()),"EKORG", param.getEkorg())
	    		 .eq(StringUtils.isNotBlank(param.getBukrs()),"BUKRS", param.getBukrs()).eq(StringUtils.isNotBlank(param.getKunnr()),"KUNNR", param.getKunnr())
	    		 .eq(StringUtils.isNotBlank(param.getLifnr()),"LIFNR", param.getLifnr()).eq(StringUtils.isNotBlank(param.getSoDocType()),"SO_DOC_TYPE", param.getSoDocType())
	    		 .eq(StringUtils.isNotBlank(param.getSoGroup()),"SO_GROUP", param.getSoGroup()).eq(StringUtils.isNotBlank(param.getSoDocType()),"PRODUCT_GROUP", param.getProductGroup())
	    		 .eq(StringUtils.isNotBlank(param.getShippingPoint()),"SHIPPING_POINT", param.getShippingPoint()).eq(StringUtils.isNotBlank(param.getSoChannel()),"SO_CHANNEL", param.getSoChannel()).eq("DEL","0"));
	    if(list.size()>0) {
	    	return R.error("该信息已维护！");
	    }
		wmsCStepLinkageService.insert(param);
		return R.ok();
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCStepLinkageEntity param) {
		wmsCStepLinkageService.updateById(param);
		return R.ok();
	}
	
	/**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCStepLinkageEntity param) {
    	
    	wmsCStepLinkageService.updateById(param);
		return R.ok();
    }
	
}

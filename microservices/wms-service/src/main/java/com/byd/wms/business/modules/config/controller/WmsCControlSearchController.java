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
import com.byd.wms.business.modules.config.entity.WmsCControlFlagEntity;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.wms.business.modules.config.service.WmsCControlFlagService;
import com.byd.wms.business.modules.config.service.WmsCControlSearchService;
import com.byd.wms.business.modules.config.service.WmsCOutRuleService;
import com.byd.wms.business.modules.config.service.WmsCoreSearchSequenceService;

/**
 * 
 * 分配存储类型搜索顺序至控制标识
 *
 */
@RestController
@RequestMapping("config/controlsearch")
public class WmsCControlSearchController {
	@Autowired
	private WmsCControlSearchService wmsCControlSearchService;
	
	@Autowired
	private WmsCControlFlagService wmsCControlFlagService;
	
	@Autowired
	private WmsCOutRuleService wmsCCOutRuleService;
	
	@Autowired
	private WmsCoreSearchSequenceService wmsCoreSearchSequenceService;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wmsCControlSearchService.queryPage(params);
		return R.ok().put("page", page);
	}
		
	@RequestMapping("/info")
	public R info(@RequestParam Long id) {
		WmsCControlSearchEntity wmsCControlSearch = wmsCControlSearchService.selectById(id);
		return R.ok().put("wmsCControlSearch", wmsCControlSearch);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {
		List<WmsCControlSearchEntity> list=wmsCControlSearchService.selectList(
	    		 new EntityWrapper<WmsCControlSearchEntity>()
	    		 .eq("WH_NUMBER", wmsCControlSearch.getWarehouseCode())
	    		 .eq("CONTROL_FLAG", wmsCControlSearch.getControlFlag())
	    		 .eq("BUSINESS_NAME", wmsCControlSearch.getWhBusinessType())
	    		 .eq("SOBKZ", wmsCControlSearch.getStockType())
	    		 .eq("LGORT", wmsCControlSearch.getLgort())
	    		 .eq("SEARCH_SEQ", wmsCControlSearch.getStorageAreaSearch())
	    		 .eq("FLAG_TYPE", wmsCControlSearch.getControlFlagType())
	             .eq("OUT_RULE", wmsCControlSearch.getOutRule())
	             .eq("DEL","0"));
	    if(list.size()>0) {
	    	return R.error("该信息已维护！");
	    }
		wmsCControlSearchService.insert(wmsCControlSearch);
		return R.ok();
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {
		wmsCControlSearchService.updateById(wmsCControlSearch);
		return R.ok();
	}
	
	/**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {
    	
    	wmsCControlSearchService.updateById(wmsCControlSearch);
		return R.ok();
    }
    
    @RequestMapping("/getControlFlag")
	public R getControlFlag(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {
		List ctlList = wmsCControlFlagService.selectList(
	    		 new EntityWrapper<WmsCControlFlagEntity>()
	    		 .eq(StringUtils.isNotBlank(wmsCControlSearch.getWarehouseCode()),"WH_NUMBER", wmsCControlSearch.getWarehouseCode())
	    		 .eq(StringUtils.isNotBlank(wmsCControlSearch.getControlFlagType()),"CONTROL_FLAG_TYPE", wmsCControlSearch.getControlFlagType())
	    		 .eq("DEL", "0"));
		return R.ok().put("ctlList", ctlList);
	}
    
    @RequestMapping("/getAreaSearch")
   	public R getAreaSearch(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {
   		List searchList = wmsCoreSearchSequenceService.selectList(
   	    		 new EntityWrapper<WmsCoreSearchSequenceEntity>()
   	    		 .eq(StringUtils.isNotBlank(wmsCControlSearch.getWarehouseCode()),"WH_NUMBER", wmsCControlSearch.getWarehouseCode())
   	    		 .eq(StringUtils.isNotBlank(wmsCControlSearch.getControlFlagType()),"SEARCH_SEQ_TYPE", wmsCControlSearch.getControlFlagType())
   	    		 .eq("DEL", "0"));
   		return R.ok().put("searchList", searchList);
   	}
    
    @RequestMapping("/getOutRule")
   	public R getOutRule(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {
   		List ruleList = wmsCCOutRuleService.selectList(
   	    		 new EntityWrapper<WmsCOutRuleEntity>()
   	    		 .eq(StringUtils.isNotBlank(wmsCControlSearch.getWarehouseCode()),"WH_NUMBER", wmsCControlSearch.getWarehouseCode())
   	    		 .eq("DEL", "0"));
   		return R.ok().put("ruleList", ruleList);
   	}
	
}

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
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleEntity;
import com.byd.wms.business.modules.config.service.WmsCOutRuleService;

/**
 * 
 * 出库规则配置
 *
 */
@RestController
@RequestMapping("config/outrule")
public class WmsCOutRuleController {
	@Autowired
	private WmsCOutRuleService wmsCOutRuleService;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wmsCOutRuleService.queryPage(params);
		return R.ok().put("page", page);
	}
		
	@RequestMapping("/info")
	public R info(@RequestParam Long id) {
		WmsCOutRuleEntity wmsCOutRule = wmsCOutRuleService.selectById(id);
		return R.ok().put("wmsCOutRule", wmsCOutRule);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCOutRuleEntity wmsCOutRule) {
		List<WmsCOutRuleEntity> list=wmsCOutRuleService.selectList(
	    		 new EntityWrapper<WmsCOutRuleEntity>()
	    		 .eq("WH_NUMBER", wmsCOutRule.getWarehouseCode()).eq("OUT_RULE", wmsCOutRule.getOutRule())
	    		 .eq("DEL","0"));
	    if(list.size()>0) {
	    	return R.error("该信息已维护！");
	    }
		wmsCOutRuleService.insert(wmsCOutRule);
		return R.ok();
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCOutRuleEntity wmsCOutRule) {
		wmsCOutRuleService.updateById(wmsCOutRule);
		return R.ok();
	}
	
	/**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCOutRuleEntity wmsCOutRule) {
    	
    	wmsCOutRuleService.updateById(wmsCOutRule);
		return R.ok();
    }
	
}

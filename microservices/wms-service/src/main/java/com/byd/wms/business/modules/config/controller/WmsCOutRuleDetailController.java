package com.byd.wms.business.modules.config.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleDetailEntity;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleEntity;
import com.byd.wms.business.modules.config.service.WmsCOutRuleDetailService;
import com.byd.wms.business.modules.config.service.WmsCOutRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 
 * 出库规则明细配置
 *
 */
@RestController
@RequestMapping("config/outruleDetail")
public class WmsCOutRuleDetailController {
	@Autowired
	private WmsCOutRuleDetailService wmsCOutRuleDetailService;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wmsCOutRuleDetailService.queryPage(params);
		return R.ok().put("page", page);
	}
		
	@RequestMapping("/info")
	public R info(@RequestParam Long id) {
		WmsCOutRuleDetailEntity wmsCOutRule = wmsCOutRuleDetailService.selectById(id);
		return R.ok().put("wmsCOutRule", wmsCOutRule);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCOutRuleDetailEntity wmsCOutRule) {
		List<WmsCOutRuleDetailEntity> list=wmsCOutRuleDetailService.selectList(
				new EntityWrapper<WmsCOutRuleDetailEntity>()
						.eq("WH_NUMBER", wmsCOutRule.getWarehouseCode())
						.eq("out_Rule",wmsCOutRule.getOutRule()));
		if(list.size()>0) {
			return R.error("出库明细字段已维护！");
		}else {
			wmsCOutRule.setDel("0");
			wmsCOutRuleDetailService.insert(wmsCOutRule);
			return R.ok();
		}
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCOutRuleDetailEntity wmsCOutRule) {
		wmsCOutRuleDetailService.updateById(wmsCOutRule);
		return R.ok();
	}
	
	/**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCOutRuleDetailEntity wmsCOutRule) {

		wmsCOutRuleDetailService.updateById(wmsCOutRule);
		return R.ok();
    }
	
}

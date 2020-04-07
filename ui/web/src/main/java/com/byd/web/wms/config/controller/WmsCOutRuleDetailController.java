package com.byd.web.wms.config.controller;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCOutRuleDetailEntity;
import com.byd.web.wms.config.entity.WmsCOutRuleEntity;
import com.byd.web.wms.config.service.WmsCOutRuleDetailRemote;
import com.byd.web.wms.config.service.WmsCOutRuleRemote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 
 * 出库规则配置
 *
 */
@RestController
@RequestMapping("config/outruleDetail")
public class WmsCOutRuleDetailController {
	@Autowired
private WmsCOutRuleDetailRemote wmsCOutRuleDetailRemote;
	@Autowired
    private UserUtils userUtils;
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		return wmsCOutRuleDetailRemote.list(params);
	}
		
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		return wmsCOutRuleDetailRemote.info(id);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCOutRuleDetailEntity wmsCOutRuleDetail) {
		wmsCOutRuleDetail.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		wmsCOutRuleDetail.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCOutRuleDetailRemote.save(wmsCOutRuleDetail);
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCOutRuleDetailEntity wmsCOutRule) {
		wmsCOutRule.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		wmsCOutRule.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCOutRuleDetailRemote.update(wmsCOutRule);
	}
	
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCOutRuleDetailEntity wmsCOutRule = new WmsCOutRuleDetailEntity();
		wmsCOutRule.setId(id);
		wmsCOutRule.setDel("X");
		
		return wmsCOutRuleDetailRemote.delById(wmsCOutRule);
	}
}

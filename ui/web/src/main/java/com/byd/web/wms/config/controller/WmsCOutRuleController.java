package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCOutRuleEntity;
import com.byd.web.wms.config.service.WmsCOutRuleRemote;

/**
 * 
 * 出库规则配置
 *
 */
@RestController
@RequestMapping("config/outrule")
public class WmsCOutRuleController {
	@Autowired
private WmsCOutRuleRemote wmsCOutRuleRemote;
	@Autowired
    private UserUtils userUtils;
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		 return wmsCOutRuleRemote.list(params);
	}
		
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		return wmsCOutRuleRemote.info(id);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCOutRuleEntity wmsCOutRule) {
		wmsCOutRule.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		wmsCOutRule.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCOutRuleRemote.save(wmsCOutRule);
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCOutRuleEntity wmsCOutRule) {
		wmsCOutRule.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		wmsCOutRule.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCOutRuleRemote.update(wmsCOutRule);
	}
	
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCOutRuleEntity wmsCOutRule = new WmsCOutRuleEntity();
		wmsCOutRule.setId(id);
		wmsCOutRule.setDel("X");
		
		return wmsCOutRuleRemote.delById(wmsCOutRule);
	}
}

package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.weaver.ast.HasAnnotation;
import org.bouncycastle.jcajce.provider.digest.GOST3411.HashMac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.service.WmsCEngineRemote;

/**
 * 
 * 发动机仓库物料日报表配置
 *
 */
@RestController
@RequestMapping("config/engine")
public class WmsCEngineController {
	@Autowired
	private WmsCEngineRemote wmsCEngineRemote;
	@Autowired
	private UserUtils userUtils;
	
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		
		 return wmsCEngineRemote.list(params);
	}
		
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody Map<String, Object> params) {
		
		params.put("EDITOR", userUtils.getUser().get("USERNAME"));
		params.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCEngineRemote.save(params);
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody Map<String, Object> params) {
		
		params.put("EDITOR", userUtils.getUser().get("USERNAME"));
		params.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCEngineRemote.update(params);
	}
	
	@RequestMapping("/info/{HEADID}/{ITEMID}")
	public R info(@PathVariable("HEADID") Long HEADID,@PathVariable("ITEMID") Long ITEMID) {
		return wmsCEngineRemote.info(HEADID,ITEMID);
	}
	
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Long HEADID,@RequestParam Long ITEMID,@RequestParam String PROJECT_CODE){
		if(ITEMID == null){
			return R.error("参数错误");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("HEADID", HEADID);
		map.put("ITEMID", ITEMID);
		map.put("HEADDEL", "X");
		map.put("ITEMDEL", "X");
		map.put("PROJECT_CODE", PROJECT_CODE);
		return wmsCEngineRemote.delete(map);
	}
}

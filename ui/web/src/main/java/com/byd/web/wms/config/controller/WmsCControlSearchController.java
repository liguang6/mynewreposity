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
import com.byd.web.wms.config.entity.WmsCControlSearchEntity;
import com.byd.web.wms.config.service.WmsCControlSearchRemote;

/**
 * 
 * 分配存储类型搜索顺序至控制标识
 *
 */
@RestController
@RequestMapping("config/controlsearch")
public class WmsCControlSearchController {
	@Autowired
private WmsCControlSearchRemote wmsCControlSearchRemote;
	@Autowired
    private UserUtils userUtils;
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		 return wmsCControlSearchRemote.list(params);
	}
		
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		return wmsCControlSearchRemote.info(id);
	}
	
	@RequestMapping("/getControlFlag")
	public R getControlFlag(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {

		return wmsCControlSearchRemote.getControlFlag(wmsCControlSearch);

	}
	@RequestMapping("/getOutRule")
	public R getOutRule(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {

		return wmsCControlSearchRemote.getOutRule(wmsCControlSearch);

	}
	@RequestMapping("/getAreaSearch")
	public R getAreaSearch(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {

		return wmsCControlSearchRemote.getAreaSearch(wmsCControlSearch);

	}
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {
		wmsCControlSearch.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		wmsCControlSearch.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCControlSearchRemote.save(wmsCControlSearch);
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCControlSearchEntity wmsCControlSearch) {
		wmsCControlSearch.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		wmsCControlSearch.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCControlSearchRemote.update(wmsCControlSearch);
	}
	
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCControlSearchEntity wmsCControlSearch = new WmsCControlSearchEntity();
		wmsCControlSearch.setId(id);
		wmsCControlSearch.setDel("X");
		wmsCControlSearchRemote.delById(wmsCControlSearch);
		return R.ok();
	}
}

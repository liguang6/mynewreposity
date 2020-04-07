package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.HashMap;
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
import com.byd.web.wms.config.service.WmsCKanbanInfoRemote;

/** 
 * 
 * @version 创建时间：2019年5月5日 上午8:42
 * 
 */
@RestController
@RequestMapping("/config/KanbanInfo")
public class WmsCKanbanInfoController {
	@Autowired
	WmsCKanbanInfoRemote wmsCKanbanInfoRemote;
	@Autowired
	private UserUtils userUtils;
	
	
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		 return wmsCKanbanInfoRemote.list(params);
	}
	
	@RequestMapping("/query")
	public R query(@RequestParam Map<String, Object> params) {
		 return wmsCKanbanInfoRemote.query(params);
	}

	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody HashMap<String, Object> params) {
//		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//		Map<String,Object> params = (Map<String,Object>)jsonObject;
		params.put("Editor",userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		params.put("Edit_Date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCKanbanInfoRemote.save(params);
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCKanbanInfoRemote.info(id);
    }
    /**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody Map<String, Object> params) {
		params.put("Editor",userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		params.put("Edit_Date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCKanbanInfoRemote.update(params);
	}
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam(value="id") Long id){
		if(id == null){
			return R.error("参数错误");
		}
		return wmsCKanbanInfoRemote.delById(id);
	}
}

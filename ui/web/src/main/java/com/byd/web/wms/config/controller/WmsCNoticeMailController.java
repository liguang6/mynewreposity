package com.byd.web.wms.config.controller;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.service.WmsCNoticeMailRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
@RestController
@RequestMapping("config/noticemail")
public class WmsCNoticeMailController {
	@Autowired
	private WmsCNoticeMailRemote WmsCNoticeMailRemote;
	@Autowired
	private UserUtils userUtils;
	
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		 return WmsCNoticeMailRemote.list(params);
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
        return WmsCNoticeMailRemote.save(params);
	}

	/**
	 *
	 * 单条查询
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		return WmsCNoticeMailRemote.info(id);
	}
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody Map<String, Object> params) {
		params.put("Editor",userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		params.put("Edit_Date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return WmsCNoticeMailRemote.update(params);
	}
	
	/**
     * 单条记录删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam(value="id") Long id){
		if(id == null){
			return R.error("参数错误");
		}
		return WmsCNoticeMailRemote.delById(id);
	}
}

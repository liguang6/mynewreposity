package com.byd.admin.modules.masterdata.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.masterdata.service.StandardWorkgroupService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月25日 下午2:37:16 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/standardWorkgroup")
public class StandardWorkgroupController {
	@Autowired
    private UserUtils userUtils;
	@Autowired
    private StandardWorkgroupService standardWorkgroupService;

	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = standardWorkgroupService.getStandardWorkgroupListByPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("EDITOR", currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	params.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	standardWorkgroupService.insertStandardWorkgroup(params);
    	return R.ok();
    }
/**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("ID", id);
    	Map<String, Object> retMap=standardWorkgroupService.selectById(param);

        return R.ok().put("result", retMap);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("EDITOR", currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	params.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	standardWorkgroupService.updateStandardWorkgroup(params);
    	return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("ID", id);
    	standardWorkgroupService.delStandardWorkgroup(param);

        return R.ok();
    }

    @RequestMapping("/getStandardWorkgroupList")
    public R getStandardWorkgroupList(@RequestParam Map<String,Object> params){
    	List<Map<String,Object>> list = standardWorkgroupService.getStandardWorkgroupList(params);
        return R.ok().put("data", list);
    }

}

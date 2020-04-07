package com.byd.web.sys.masterdata.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.masterdata.service.StandardWorkgroupRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月20日 上午10:07:27 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/StandardWorkgroup")
public class StandardWorkgroupController {
	@Autowired
    private StandardWorkgroupRemote standardWorkgroupRemote;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return standardWorkgroupRemote.list(params);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return standardWorkgroupRemote.info(id);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	return standardWorkgroupRemote.save(params);
    }

    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
        return standardWorkgroupRemote.update(params);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	return standardWorkgroupRemote.deleteById(id);
    }

    @RequestMapping("/getStandardWorkgroupList")
    public R getStandardWorkgroupList(@RequestParam Map<String,Object> map){
    	return standardWorkgroupRemote.getStandardWorkgroupList(map);
    }
}

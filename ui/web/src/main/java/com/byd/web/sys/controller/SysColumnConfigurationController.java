package com.byd.web.sys.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.service.SysColumnConfigurationRemote;

/**
 * @ClassName SysDataPermissionController
 * @Description  数据权限管理
 * @author develop01 
 * @since 3.1.0 2019-03-21
 */
@RestController
@RequestMapping("sys/columnConfiguration")
public class SysColumnConfigurationController {
    @Autowired
    private SysColumnConfigurationRemote sysColumnConfigurationRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return sysColumnConfigurationRemote.list(params);
    }
    
    @RequestMapping("/usergridlist")
    public R usergridlist(@RequestParam Map<String, Object> params){
    	params.put("userId", userUtils.getUser().get("USER_ID"));
        return sysColumnConfigurationRemote.usergridlist(params);
    }
    
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params) {
    	return sysColumnConfigurationRemote.save(params);
    }
    
    @RequestMapping("/delete")
    public R delete(@RequestParam Map<String, Object> params) {
    	return sysColumnConfigurationRemote.delete(params);
    }
    
    @RequestMapping("/userSave")
    public R userSave(@RequestParam Map<String, Object> params) {
    	return sysColumnConfigurationRemote.userSave(params);
    }

    @RequestMapping("/trans")
    public R trans(@RequestParam Map<String, Object> params) {
	   	params.put("userId", userUtils.getUser().get("USER_ID"));
	   	return sysColumnConfigurationRemote.trans(params);
    }
    
}


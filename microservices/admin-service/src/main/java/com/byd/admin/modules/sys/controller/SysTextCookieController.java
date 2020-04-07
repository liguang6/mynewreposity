package com.byd.admin.modules.sys.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.sys.entity.SysUserEntity;
import com.byd.admin.modules.sys.service.SysTextCookieService;
import com.byd.utils.R;

/**
 * @ClassName SysDataPermissionController
 * @Description  数据权限管理
 * @Author qiu.jiaming1
 * @Date 2019/2/26
 */
@RestController
@RequestMapping("sys/sysTextCookie")
public class SysTextCookieController {

    @Autowired
    private SysTextCookieService sysTextCookieService;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	SysUserEntity user = null;
    	params.put("userId", user.getUserId());
        String  inputValue = sysTextCookieService.queryPage(params);
        return R.ok().put("page", inputValue);
    }
    
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	SysUserEntity user = null;
    	params.put("userId", user.getUserId());
        sysTextCookieService.saveInputVal(params);
        return R.ok();
    }
    }


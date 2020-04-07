package com.byd.web.sys.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.sys.service.SysTableConfigurationRemote;

/**
 * @ClassName SysDataPermissionController
 * @Description  数据权限管理
 * @Author qiu.jiaming1
 * @Date 2019/2/26
 */
@RestController
@RequestMapping("sys/tableConfiguration")
public class SysTableConfigurationController {

    @Autowired
    private SysTableConfigurationRemote sysTableConfigurationRemote;
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return sysTableConfigurationRemote.list(params);
    }
}


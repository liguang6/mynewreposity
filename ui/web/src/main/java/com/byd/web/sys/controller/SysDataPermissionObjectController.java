package com.byd.web.sys.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.sys.entity.SysDataPermissionObjectEntity;
import com.byd.web.sys.service.SysDataPermissionObjectRemote;

/**
 * @ClassName SysDataPermissionObjectController
 * @Description  数据权限对象管理
 * @Author qiu.jiaming1
 * @Date 2019/3/14
 */
@RestController
@RequestMapping("sys/dataPermissionObject")
public class SysDataPermissionObjectController {

    @Autowired
    private SysDataPermissionObjectRemote sysDataPermissionObjectRemote;
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return sysDataPermissionObjectRemote.list(params);
    }
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
    	return sysDataPermissionObjectRemote.info(id);
    }

    /**
     * 保存
     * @param storage
     * @return
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysDataPermissionObjectEntity storage){
    	return sysDataPermissionObjectRemote.save(storage);
    }

    /**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysDataPermissionObjectEntity wh){
    	return sysDataPermissionObjectRemote.update(wh);
    }

    /**
     * 软删BY ID
     */
    @RequestMapping("/delete")
    public R delById(Long id){
    	return sysDataPermissionObjectRemote.delById(id);
    }


}

package com.byd.web.sys.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.sys.entity.SysDataPermissionEntity;
import com.byd.web.sys.service.SysDataPermissionRemote;

/**
 * @ClassName SysDataPermissionController
 * @Description  数据权限管理
 * @author develop01 
 * @since 3.1.0 2019-03-21
 */
@RestController
@RequestMapping("sys/dataPermission")
public class SysDataPermissionController {

    @Autowired
    private SysDataPermissionRemote sysDataPermissionRemote;
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return sysDataPermissionRemote.list(params);
    }
    
    @RequestMapping("/userPermissionlist")
	@ResponseBody
    public R userPermissionlist(@RequestParam Map<String, Object> params){
        return sysDataPermissionRemote.userAuthlist(params);
    }
    
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        return sysDataPermissionRemote.info(id);
    }

    /**
     * 单条记录查询By menuID
     */
    @RequestMapping("/getAuthFields")
    public R getAuthFields(@RequestParam Map<String, Object> params){
    	return sysDataPermissionRemote.getAuthFields(params);
    }

    /**
     * 保存
     * @param storage
     * @return
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysDataPermissionEntity storage){
    	return sysDataPermissionRemote.save(storage);
    }

    /**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysDataPermissionEntity wh){
    	return sysDataPermissionRemote.update(wh);
    }

    /**
     * 软删BY ID
     */
    @RequestMapping("/delete")
    public R delById(Long id){
    	return sysDataPermissionRemote.delById(id);
    }

    /**
     * 保存
     * @param storage
     * @return
     */
    @RequestMapping("/userAuthSave")
    public R userAuthSave(@RequestBody Map<String, Object> params){
    	return sysDataPermissionRemote.userAuthSave(params);
    }
    
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/userAuthInfo")
    public R userAuthInfo(@RequestParam Long id){
        return sysDataPermissionRemote.userAuthInfo(id);
    }
    
    /**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping("/userAuthUpdate")
    public R userAuthUpdate(@RequestBody Map<String, Object> params){
    	return sysDataPermissionRemote.userAuthUpdate(params);
    }
    
    /**
     * 软删BY ID
     */
    @RequestMapping("/userAuthDelById")
    public R userAuthDelById(Long id){
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("ids", id);
    	return sysDataPermissionRemote.userAuthDelById(params);
    }
}

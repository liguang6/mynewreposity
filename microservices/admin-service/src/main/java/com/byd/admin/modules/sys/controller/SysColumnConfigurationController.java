package com.byd.admin.modules.sys.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.byd.admin.common.annotation.SysLog;
import com.byd.admin.modules.sys.entity.SysUserEntity;
import com.byd.admin.modules.sys.service.SysColumnConfigurationService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;

/**
 * @ClassName SysDataPermissionController
 * @Description  数据权限管理
 * @Author qiu.jiaming1
 * @Date 2019/2/26
 */
@RestController
@RequestMapping("sys/columnConfiguration")
public class SysColumnConfigurationController {

    @Autowired
    private SysColumnConfigurationService columnConfigurationService;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = columnConfigurationService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    @RequestMapping("/usergridlist")
    public R usergridlist(@RequestParam Map<String, Object> params){
    	params.put("userId", userUtils.getUser().get("USER_ID"));
        PageUtils page = columnConfigurationService.queryUserGridPage(params);
        return R.ok().put("page", page);
    }
    
    @RequestMapping("/save")
    @SysLog("表格管理-保存")
    public R save(@RequestParam Map<String, Object> params) {
    	String columnListStr=params.get("columnList").toString(); 	
    	List<Map> columnList = JSONObject.parseArray(columnListStr, Map.class);
    	columnConfigurationService.saveColumnConfiguration(columnList);
    	return R.ok("保存成功！");
    }
    
    @RequestMapping("/delete")
    @SysLog("表格管理-删除行")
    public R delete(@RequestParam Map<String, Object> params) {
    	String columnListStr=params.get("columnList").toString();
    	
    	List<Map> columnList = JSONObject.parseArray(columnListStr, Map.class);
    	columnConfigurationService.deleteUserConfiguration(columnList);
    	
    	return R.ok("保存成功！");
    }
    
    @RequestMapping("/userSave")
    @SysLog("用户自定义表格管理-保存")
    public R userSave(@RequestParam Map<String, Object> params) {
    	columnConfigurationService.saveUserConfiguration(params);
    	return R.ok("保存成功！");
    }

    @RequestMapping("/trans")
    @SysLog("表格管理-保存")
    public R trans(@RequestParam Map<String, Object> params) {
    	params.put("userId", userUtils.getUser().get("USER_ID"));
        PageUtils page = columnConfigurationService.queryUserGridPage(params);
        return R.ok().put("list", page.getList());
//    	return R.ok("保存成功！");
    }
    
    }


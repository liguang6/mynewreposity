package com.byd.admin.modules.sys.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysUserAuthEntity;
import com.byd.admin.modules.sys.service.SysDataPermissionService;
import com.byd.admin.modules.sys.service.SysUserAuthService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

/**
 * @ClassName SysDataPermissionController
 * @Description  数据权限管理
 * @Author qiu.jiaming1
 * @Date 2019/2/26
 */
@RestController
@RequestMapping("sys/dataPermission")
public class SysDataPermissionController {

    @Autowired
    private SysDataPermissionService sysDataPermissionService;
    @Autowired
    private SysUserAuthService sysUserAuthService;
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysDataPermissionService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        List<Map<String,Object>> list = sysDataPermissionService.selectSysDataPermissionById(id);
        return R.ok().put("dataPermissionResult", list.get(0));
    }

    /**
     * 单条记录查询By menuID
     */
    @RequestMapping("/getAuthFields")
    public R getAuthFields(@RequestParam Map<String, Object> params){
        String menuId =String.valueOf(params.get("menuId"));
        if(menuId==null || menuId.equals("")){
            return R.ok().put("dataPermissionResult", null);
        }
        List<Map<String,Object>> list = sysDataPermissionService.getAuthFields(menuId);
        return R.ok().put("dataPermissionResult", list);
    }

    /**
     * 保存
     * @param storage
     * @return
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysDataPermissionEntity storage){
        List<SysDataPermissionEntity> list=sysDataPermissionService.selectList(
                new EntityWrapper<SysDataPermissionEntity>()
                        .eq("role_id", storage.getRoleId())
                        .eq("user_id",storage.getUserId())
                        .eq("menu_id",storage.getMenuId())
                        .eq("auth_fields",storage.getAuthFields()));
        if(list.size()>0) {
            return R.error("数据权限字段已维护！");
        }else {
            storage.setDel("0");
            sysDataPermissionService.insert(storage);
            return R.ok();
        }
    }

    /**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysDataPermissionEntity wh){
        sysDataPermissionService.updateById(wh);
        return R.ok();
    }

    /**
     * 软删BY ID
     */
    @RequestMapping("/delete")
    public R delById(Long id){
        if(id == null){
            return R.error("参数错误");
        }
        SysDataPermissionEntity params = new SysDataPermissionEntity();
        params.setId(id);
        params.setDel("X");
        sysDataPermissionService.updateById(params);
        return R.ok();
    }

    
    
    @RequestMapping("/queryDataPermissionByMenuKey")
    public List<Map<String,Object>> queryDataPermissionByMenuKey(@RequestParam Map<String,Object> params){
        return sysDataPermissionService.queryDataPermissionByMenuKey(params);
    }
    
    /**
     * 分页查询
     */
    @RequestMapping("/userAuthlist")
    public R userAuthlist(@RequestParam Map<String, Object> params){
        PageUtils page = sysUserAuthService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    /**
     * 用户权限保存
     * @param storage
     * @return
     */
    @RequestMapping("/userAuthSave")
    public R userAuthSave(@RequestBody Map<String, Object> params){
//        List<SysDataPermissionEntity> list=sysDataPermissionService.selectList(
//                new EntityWrapper<SysDataPermissionEntity>()
//                        .eq("role_id", storage.getRoleId())
//                        .eq("user_id",storage.getUserId())
//                        .eq("menu_id",storage.getMenuId())
//                        .eq("auth_fields",storage.getAuthFields()));
//        if(list.size()>0) {
//            return R.error("数据权限字段已维护！");
//        }else {
//            storage.setDel("0");
    	SysUserAuthEntity userAuth = new SysUserAuthEntity();
    	userAuth = JSON.parseObject(JSON.toJSONString(params), SysUserAuthEntity.class);
    	sysUserAuthService.insert(userAuth);
            return R.ok();
//        }
    }
    
    /**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping("/userAuthUpdate")
    public R userAuthUpdate(@RequestBody Map<String, Object> params){
    	SysUserAuthEntity userAuth = new SysUserAuthEntity();
    	userAuth = JSON.parseObject(JSON.toJSONString(params), SysUserAuthEntity.class);
    	sysUserAuthService.updateById(userAuth);
        return R.ok();
    }

    
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/userAuthInfo")
    public R userAuthInfo(@RequestParam Long id){
    	Map<String,Object> userAuth = sysUserAuthService.selectSysUserPermissionById(id);
        return R.ok().put("dataPermissionResult", userAuth);
    }
    
    /**
     * 软删BY ID
     */
    @RequestMapping("/userAuthDelById")
    public R userAuthDelById(@RequestParam Map<String, Object> params){
    	String ids=params.get("ids").toString();
    	if(ids == null){
            return R.error("参数错误");
        }
//    	SysUserAuthEntity userAuth = new SysUserAuthEntity();
//    	userAuth.setId(Long.valueOf(ids));
    	sysUserAuthService.deleteById(Long.parseLong(ids));
    	return R.ok();
    }
}

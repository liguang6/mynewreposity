package com.byd.admin.modules.sys.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.admin.modules.sys.entity.SysDataPermissionObjectEntity;
import com.byd.admin.modules.sys.service.SysDataPermissionObjectService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

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
    private SysDataPermissionObjectService sysDataPermissionObjectService;
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        System.out.println(params.toString());
        PageUtils page = sysDataPermissionObjectService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        List<Map<String,Object>> list = sysDataPermissionObjectService.selectSysDataPermissionById(id);
        return R.ok().put("dataPermissionResultObject", list.get(0));
    }

    /**
     * 保存
     * @param storage
     * @return
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysDataPermissionObjectEntity storage){
        List<SysDataPermissionObjectEntity> list=sysDataPermissionObjectService.selectList(
                new EntityWrapper<SysDataPermissionObjectEntity>()
                        .eq("menu_id",storage.getMenuId())
                        .eq("auth_fields",storage.getAuthFields())
                        .eq("del","0"));
        if(list.size()>0) {
            return R.error("数据权限字段已维护！");
        }else {
            storage.setDel("0");
            sysDataPermissionObjectService.insert(storage);
            return R.ok();
        }
    }

    /**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysDataPermissionObjectEntity wh){
        sysDataPermissionObjectService.updateById(wh);
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
        SysDataPermissionObjectEntity params = new SysDataPermissionObjectEntity();
        params.setId(id);
        params.setDel("X");
        sysDataPermissionObjectService.updateById(params);
        return R.ok();
    }


}

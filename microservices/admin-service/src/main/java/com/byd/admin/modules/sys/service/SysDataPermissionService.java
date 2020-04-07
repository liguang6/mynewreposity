package com.byd.admin.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.utils.PageUtils;

import java.util.List;
import java.util.Map;

public interface SysDataPermissionService extends IService<SysDataPermissionEntity> {

    List<SysDataPermissionEntity> queryDataPermission(Map<Long, String> params);

    PageUtils queryPage(Map<String, Object> params);

    void delete(SysDataPermissionEntity entity);

    List<Map<String,Object>> selectSysDataPermissionById(Long id);

    List<Map<String, Object>> getAuthFields(String id);
    
    List<Map<String, Object>> queryDataPermissionByMenuKey(Map<String, Object> params);
}

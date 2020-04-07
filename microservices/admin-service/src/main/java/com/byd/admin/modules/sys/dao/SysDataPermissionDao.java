package com.byd.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysMenuEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SysDataPermissionDao extends BaseMapper<SysDataPermissionEntity> {
     List<SysDataPermissionEntity> queryDataPermission(Map<Long,String> params);
     List<Map<String,Object>> queryDataPermissionList(Map<String,Object> params);
    List<Map<String,Object>> selectSysDataPermissionById(Map idMap);

    int getSysDataPermissionCount(Map<String, Object> params);

    List<Map<String, Object>> getAuthFields(Map map);
    
    List<Map<String,Object>> queryDataPermissionByMenuKey(Map<String,Object> params);
}

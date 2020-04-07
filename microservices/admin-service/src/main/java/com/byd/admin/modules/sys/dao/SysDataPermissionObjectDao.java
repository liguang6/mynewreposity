package com.byd.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysDataPermissionObjectEntity;

import java.util.List;
import java.util.Map;

public interface SysDataPermissionObjectDao extends BaseMapper<SysDataPermissionObjectEntity> {
     List<Map<String,Object>> queryDataPermissionObjectList(Map<String, Object> params);
    List<Map<String,Object>> selectSysDataPermissionObjectById(Map idMap);

    int getSysDataPermissionObjectCount(Map<String, Object> params);


}

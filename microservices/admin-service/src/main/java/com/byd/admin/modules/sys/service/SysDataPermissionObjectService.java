package com.byd.admin.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysDataPermissionObjectEntity;
import com.byd.utils.PageUtils;

import java.util.List;
import java.util.Map;

public interface SysDataPermissionObjectService extends IService<SysDataPermissionObjectEntity> {


    PageUtils queryPage(Map<String, Object> params);

    void delete(SysDataPermissionEntity entity);

    List<Map<String,Object>> selectSysDataPermissionById(Long id);
}

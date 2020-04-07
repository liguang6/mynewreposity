package com.byd.admin.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import com.byd.admin.modules.sys.entity.SysTableConfigurationEntity;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SysTableConfigurationService extends IService<SysMenuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

package com.byd.admin.modules.sys.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysUserAuthEntity;
import com.byd.utils.PageUtils;

public interface SysUserAuthService extends IService<SysUserAuthEntity>{
	
	PageUtils queryPage(Map<String, Object> params);
	
	Map<String,Object> selectSysUserPermissionById(Long id);
}

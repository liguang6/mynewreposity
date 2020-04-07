package com.byd.admin.modules.sys.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.sys.entity.SysUserAuthEntity;

public interface SysUserAuthDao extends BaseMapper<SysUserAuthEntity> {

	 List<Map<String,Object>> queryUserPermissionList(Map<String,Object> params);
	 
	 Map<String,Object> selectSysUserPermissionById(Map idMap);
}

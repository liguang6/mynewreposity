package com.byd.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.sys.entity.SysColumnConfigurationEntity;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import com.byd.admin.modules.sys.entity.SysTableConfigurationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SysColumnConfigurationDao extends BaseMapper<SysColumnConfigurationEntity> {
     List queryColumnConfiguration(Map<String,String> params);

	List<Map<String, Object>> queryUserGrid(Map<String, Object> params);

	int getUserGridPageCount(Map<String, Object> params);

	List<String> queryGridIdByGridNo(String gridNo);

	List<Map<String, Object>> queryUserGridDefault(Map<String, Object> params);

	int getUserGridPageDefaultCount(Map<String, Object> params);

	int queryUserConfiguration(Map<String, Object> params);

	void insertUserConfiguration(Map<String, Object> usermap);

	void updateUserConfiguration(Map<String, Object> usermap);

}

package com.byd.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import java.util.List;
import java.util.Map;

public interface SysTableConfigurationDao extends BaseMapper<SysMenuEntity> {

	List<Map<String, Object>> queryGridByName(Map<String, Object> params);

	int getGridPageCount(Map<String, Object> params);

}

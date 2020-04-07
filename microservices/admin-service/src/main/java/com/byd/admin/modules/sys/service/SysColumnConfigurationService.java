package com.byd.admin.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysColumnConfigurationEntity;
import com.byd.utils.PageUtils;

import java.util.List;
import java.util.Map;

public interface SysColumnConfigurationService extends IService<SysColumnConfigurationEntity> {

    List<SysColumnConfigurationEntity>ColumnConfiguration(Map<String, String> params);

    PageUtils queryPage(Map<String, Object> params);
    
    PageUtils queryUserGridPage(Map<String, Object> params); 
    
    public void saveColumnConfiguration(List<Map> params);
    
	void saveUserConfiguration(Map<String, Object> columnList);
	
	void deleteUserConfiguration(List<Map> columnList);
}

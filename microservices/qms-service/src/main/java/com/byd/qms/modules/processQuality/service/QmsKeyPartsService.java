package com.byd.qms.modules.processQuality.service;

import com.byd.utils.PageUtils;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 品质 关键零部件
 * @author cscc tangj
 * @email 
 * @date 2019-07-30 10:57:57
 */
public interface QmsKeyPartsService{
	// 大巴关键零部件分页查询
    PageUtils queryPage(Map<String, Object> params);
    
    public List<Map<String,Object>> getList(Map<String, Object> params);
    
    public List<Map<String,Object>> getOrderConfigList(Map<String, Object> params);
    
    public List<Map<String,Object>> getWorkshopList(Map<String,Object> params);
    // 专用车关键零部件分页查询
    PageUtils queryPage02(Map<String, Object> params);
    
    List<Map<String,Object>> getList02(Map<String, Object> params);
    
    public List<Map<String,Object>> getOrderConfigList02(Map<String, Object> params);
    
    public List<Map<String,Object>> getLineNameList(Map<String,Object> params);
    
    // 大巴关键零部件追溯
	
    PageUtils getBmsTraceBackList(Map<String,Object> params);
    
    // 专用车关键零部件追溯
	
    PageUtils getVmesTraceBackList(Map<String,Object> params);
	
        
}


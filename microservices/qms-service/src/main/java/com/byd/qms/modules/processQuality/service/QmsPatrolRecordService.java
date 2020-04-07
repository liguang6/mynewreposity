package com.byd.qms.modules.processQuality.service;

import com.byd.utils.PageUtils;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 品质巡检记录
 * @author cscc tangj
 * @email 
 * @date 2019-07-26 10:57:57
 */
public interface QmsPatrolRecordService{

    PageUtils queryPage(Map<String, Object> params);
    
    Map<String, Object> getById(Long id);
    
    String save(Map<String, Object> params);
    
    int update(Map<String, Object> params);
    
    int delete(String tempNo);
    
    public List<Map<String,Object>> getTemplateList(Map<String, Object> params);
    
    public List<Map<String,Object>> getList(Map<String, Object> params);
    
}


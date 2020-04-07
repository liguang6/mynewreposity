package com.byd.qms.modules.processQuality.service;

import com.byd.utils.PageUtils;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 品质检验模板
 * @author cscc tangj
 * @email 
 * @date 2019-07-24 10:57:57
 */
public interface QmsCheckTemplateService{

    PageUtils queryPage(Map<String, Object> params);
    
    Map<String, Object> getById(Long id);
    
    String save(Map<String, Object> params);
    
    String saveOrUpdate(Map<String, Object> params);
    
    int update(Map<String, Object> params);
    
    int delete(String tempNo);
    
    int deleteItemById(Long id);
    
    public List<Map<String,Object>> getItemList(Map<String, Object> params);
    
    public int checkTempIsUsed(String tempNo);
   
}


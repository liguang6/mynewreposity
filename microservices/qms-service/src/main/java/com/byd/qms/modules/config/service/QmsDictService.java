package com.byd.qms.modules.config.service;

import com.byd.utils.PageUtils;
import java.util.List;
import java.util.Map;

/**
 * 品质数据字典
 *
 * @author cscc tangj
 * @email 
 * @date 2019-07-29 13:57:57
 */
public interface QmsDictService{

    PageUtils queryPage(Map<String, Object> params);
    
    Map<String, Object> getById(Long id);
    
    int save(Map<String, Object> params);
    
    int update(Map<String, Object> params);
    
    int delete(Long id);
    
    public List<Map<String,Object>> getList(Map<String,Object> params);
   
}


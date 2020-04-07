package com.byd.qms.modules.config.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 车型
 * @author cscc tangj
 * @email 
 * @date 2019-07-30 13:57:57
 */
public interface QmsBusTypeDao {
	
    public List<Map<String,Object>> getListByPage(Map<String,Object> params);
	
	public int getListCount(Map<String,Object> params);
	
	public Map<String,Object> getById(@Param("id") Long id);
	
	public int save(Map<String,Object> params);
	
	public int update(Map<String,Object> params);
	
	public int delete(@Param("id") Long id);
	
	public List<Map<String,Object>> getList(Map<String,Object> params);
	
}

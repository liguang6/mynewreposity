package com.byd.qms.modules.processQuality.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
/**
 * 品质模板
 * @author tangj
 * @email 
 * @date 2019-07-24 10:12:08
 */
public interface QmsCheckTemplateDao{


    public List<Map<String,Object>> getListByPage(Map<String,Object> params);
    
    public List<Map<String,Object>> getItemList(Map<String, Object> params);
	
	public int getListCount(Map<String,Object> params);
	
	public int saveHead(Map<String,Object> params);
	
	public int saveItem(Map<String,Object> params);
	
	public int updateItem(Map<String,Object> params);
	
	public Integer getMaxItemNo(@Param("tempNo") String tempNo);
	
	public int deleteHead(@Param("tempNo") String tempNo);
	
	public int deleteItem(@Param("tempNo") String tempNo);
	
	public int deleteItemById(@Param("id") Long id);
	// 查询模板是否被引用；
	public int checkTempIsUsed(@Param("tempNo") String tempNo);
}

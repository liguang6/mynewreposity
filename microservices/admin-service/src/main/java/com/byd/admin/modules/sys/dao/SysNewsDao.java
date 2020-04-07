package com.byd.admin.modules.sys.dao;


import java.util.List;
import java.util.Map;


/**
 * 
 * 控制标识配置
 *
 */
public interface SysNewsDao{

	 	int getListCount(Map<String, Object> params);

	    List<Map<String, Object>> queryNoticeMailList(Map<String, Object> params);
	 	
	 	List<Map<String, Object>> query(Map<String, Object> params);

	    void insertNoticeMail(Map<String, Object> params);

	    Map<String, Object> selectById(Long id);

	    void updateById(Map<String, Object> params);

	    void delById(Long id);
	    
	    List<Map<String,Object>> getUserNews(Map<String, Object> params);
}

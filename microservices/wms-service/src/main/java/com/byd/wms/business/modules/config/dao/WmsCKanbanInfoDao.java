package com.byd.wms.business.modules.config.dao;


import java.util.List;
import java.util.Map;


/**
 * 
 * 控制标识配置
 *
 */
public interface WmsCKanbanInfoDao{

	 	int getListCount(Map<String, Object> params);

	    List<Map<String, Object>> queryNoticeMailList(Map<String, Object> params);
	 	
	 	List<Map<String, Object>> query(Map<String, Object> params);

	    void insertNoticeMail(Map<String, Object> params);

	    Map<String, Object> selectById(Long id);

	    void updateById(Map<String, Object> params);

	    void delById(Long id);
}

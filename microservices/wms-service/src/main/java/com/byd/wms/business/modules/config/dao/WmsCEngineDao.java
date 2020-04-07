package com.byd.wms.business.modules.config.dao;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 *
 */
public interface WmsCEngineDao {

	List<Map<String, Object>> queryAll(Map<String, Object> params);
	
	List<Map<String, Object>> selectAll(Map<String, Object> params);
	
	List<Map<String, Object>> selectItemAll(Map<String, Object> params);

	Map<String, Object> selectById(Map<String, Object> params);

	void updateById(Map<String, Object> params);
	
	void updateItemById(Map<String, Object> params);
	
	void delById(Long id);

	void insert(Map<String, Object> params);
	
	void insertItem(Map<String, Object> params);

	List<Map<String, Object>> selectByProject(Map<String, Object> params);

	void updateProject(Map<String, Object> params);

}

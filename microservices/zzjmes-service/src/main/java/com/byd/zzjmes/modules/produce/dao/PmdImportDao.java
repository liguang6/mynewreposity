package com.byd.zzjmes.modules.produce.dao;

import java.util.List;
import java.util.Map;
/**
 * 机台计划
 * @author tangj
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface PmdImportDao{

	Map<String, Object> queryPmdHeader(Map<String, Object> pmd);
	List<Map<String, Object>> queryPmdItems(Map<String, Object> condMap);
	
	public int addPmdHeader(Map<String, Object> condMap);
	public int addPmdDetails(Map<String, Object> condMap);
	public int addPmdHistoryDetails(Map<String, Object> condMap);
	
}

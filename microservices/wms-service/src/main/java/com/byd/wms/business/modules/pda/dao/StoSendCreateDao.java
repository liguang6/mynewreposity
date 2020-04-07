package com.byd.wms.business.modules.pda.dao;

import java.util.List;
import java.util.Map;

public interface StoSendCreateDao {
	
	public List<Map<String, Object>> list(Map<String, Object> params);
	
	public int insertHead(Map<String, Object> params);
	
	public int insertItem(List<Map<String, Object>> params);
	
	public int insertPacking(List<Map<String, Object>> params);
	
	public List<Map<String, Object>> querySto(Map<String, Object> params);
	
	public List<Map<String, Object>> queryContact(Map<String, Object> params);
	
	public List<Map<String, Object>> queryCustomer(Map<String, Object> params);
	
	public List<Map<String, Object>> queryWMSNo(Map<String, Object> params);
	
	public List<Map<String, Object>> queryLiktx(Map<String, Object> params);
	
	public List<Map<String, Object>> checkAddr(Map<String, Object> params);
	
	public List<Map<String, Object>> checkExist(Map<String, Object> params);
	
	public Map<String, Object> queryBydeliveryNo(Map<String, Object> params);
	
	
}

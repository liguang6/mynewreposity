package com.byd.wms.business.modules.pda.service;

import java.util.List;
import java.util.Map;


public interface StoSendCreateService {

	public List list(Map<String, Object> params);
	
	public String create(Map<String, Object> params);
	
	public List querySto(Map<String, Object> params);
	
	public List queryCustomer(Map<String, Object> params);
	
	public List queryWMSNo(Map<String, Object> params);
	
	public List queryContact(Map<String, Object> params);
	
	public List queryLiktx(Map<String, Object> params);
	
	public List checkAddr(Map<String, Object> params);
	
	public List checkExist(Map<String, Object> params);
	
	public List queryBydeliveryNo(List<Map<String, Object>> params);
	
}

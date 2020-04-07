package com.byd.wms.business.modules.inpda.service;

import java.util.Map;

import com.byd.utils.PageUtils;

public interface WmsAutoPutawayPdaService {

	public Map<String, Object> scan(Map<String, Object> params);
	
	public PageUtils docItem(Map<String, Object> params);
	
	public PageUtils labelItem(Map<String, Object> params);
	
	public void deleteLabel(Map<String, Object> params);
	
	public Map<String, Object> confirm(Map<String, Object> params);
}

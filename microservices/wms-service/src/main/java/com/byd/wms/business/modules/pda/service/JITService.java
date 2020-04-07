package com.byd.wms.business.modules.pda.service;

import java.util.List;
import java.util.Map;


public interface JITService {

	public List JITScanLabel(Map<String, Object> params);
	
	public List JITScanDeliveryNo(Map<String, Object> params);
	
	public void JITPick(Map<String, Object> params);
		
	
}

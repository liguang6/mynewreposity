package com.byd.sap.service;

import java.util.Map;

public interface IWmsAutoPutawayService {

	public Map<String,Object> createStoPO(Map<String,Object> params);
	
	public Map<String,Object> createStoDN(Map<String,Object> params);
	
	public Map<String,Object> postDN(String werks,String vbeln);
}

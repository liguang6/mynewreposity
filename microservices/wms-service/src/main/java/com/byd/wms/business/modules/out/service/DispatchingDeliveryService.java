package com.byd.wms.business.modules.out.service;

import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;

import java.util.List;
import java.util.Map;

import org.tempuri.WMSDispatchingReturnResult;



public interface DispatchingDeliveryService {

	 PageUtils list(Map<String, Object> params);    
	 
	 String delivery(List<Map<String, Object>> params) throws Exception;
}


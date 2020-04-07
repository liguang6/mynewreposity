package com.byd.wms.business.modules.inpda.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.in.service.SCMDeliveryService;
import com.byd.wms.business.modules.inpda.service.WmsAsnReceiptService;

@Service("wmsAsnReceiptService")
public class WmsAsnReceiptServiceImpl implements WmsAsnReceiptService{
	
	@Autowired
	private CommonService commonService;
	@Autowired
	private SCMDeliveryService scmDeliveryService;
	
	@Override
	public Map<String, Object> scan(Map<String, Object> params){
		Map<String, Object> resMap=new HashMap<String, Object>();
		
		
		List<Map<String,Object>> labellist = scmDeliveryService.getScmBarCodeInfo(params);
		
		List<Map<String,Object>> labelCachelist = commonService.getLabelCacheInfo(params);
    	for (Map<String,Object> labelCache:labelCachelist) {
    		if (!labelCache.get("REF_DOC_NO").equals(labellist.get(0).get("ASSNO"))) {
    			throw new IllegalArgumentException("送货单："+labelCache.get("REF_DOC_NO")+"未完成");
    		}
    		if (labelCache.get("LABEL_NO").equals(params.get("LABEL_NO"))) {
        		throw new IllegalArgumentException("已存在，勿重复扫描！");
        	}
    	}
		
		int boxs = commonService.getLabelCacheInfoCount(params);
    	
    	resMap.put("result", labellist);
    	resMap.put("boxs", boxs);
    	return resMap;
	}

}

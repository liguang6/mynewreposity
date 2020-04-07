package com.byd.wms.business.modules.qc.service;

import java.util.List;
import java.util.Map;

public interface WmsQcStockRejudge {
	
	public void saveStockRejudgeNotInspect(List<Map<String,Object>> items) throws IllegalAccessException;

	
	public void saveStockRejudgeOnInspect(List<Map<String,Object>> items);
}

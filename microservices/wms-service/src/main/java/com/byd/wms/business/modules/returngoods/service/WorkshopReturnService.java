package com.byd.wms.business.modules.returngoods.service;

import java.util.List;
import java.util.Map;

public interface WorkshopReturnService {
	public List<Map<String, Object>> getBusinessNameListByWerks(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnConfirmData(Map<String, Object> params);
	public List<Map<String, Object>> getSapMoComponentDate(Map<String, Object> params);
	public String createWorkshopReturn(Map<String, Object> params);
	public String confirmWorkshopReturn(Map<String, Object> params);
	public List<Map<String, Object>> getPlantInfoByWerks(Map<String, Object> params);
	public List<Map<String, Object>> getSapVendorInfo(Map<String, Object> params);
	public int getMoHeadHxQty(String AUFNR);
	public int getSapMoCount(Map<String, Object> params);
	public int getSapPoCount(Map<String, Object> params);
	public List<Map<String, Object>> getSapPoStatus(Map<String, Object> params);
	public String getSapMoStatus(Map<String, Object> params);
	public List<Map<String,Object>> getMatBatch(Map<String, Object> params);
}

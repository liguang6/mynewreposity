package com.byd.wms.business.modules.returngoods.dao;

import java.util.List;
import java.util.Map;

public interface WorkshopReturnDao {
	public List<Map<String, Object>> getBusinessNameListByWerks(Map<String, Object> params);
	public List<Map<String, Object>> getSapVendorInfo(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData36(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData37(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData38(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData39(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData40(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData46(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData68(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData69(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData70(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData71(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnData72(Map<String, Object> params);
	
	public List<Map<String, Object>> getWorkshopReturnConfirmData(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnMatInfoList(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnPickingData(Map<String, Object> params);
	
	public int updateOutReturnHead(Map<String, Object> params);
	public int updateOutReturnItem(Map<String, Object> params);
	public int insertCoreLabel(List<Map<String, Object>> labelList);
	public List<Map<String, Object>> getPutAwayFlag(Map<String, Object> params);
	public List<Map<String, Object>> getOutReturnItemDetail(Map<String, Object> params);
	public int insertOutReturnItemDetail(Map<String, Object> params);
	public int updateOutReturnItemDetail(Map<String, Object> params);
	public int updateCoreStockQty(Map<String, Object> params);
	public int insertCoreStockQty(Map<String, Object> params);
	public int updateMOComponentQty(Map<String, Object> params);
	
	public List<Map<String, Object>> getSapMoComponentDate(Map<String, Object> params);
	public List<Map<String, Object>> getSapMoComponentDate35(Map<String, Object> params);
	public List<Map<String, Object>> getSapMoComponentDate36(Map<String, Object> params);
	public List<Map<String, Object>> getSapMoComponentDate37(Map<String, Object> params);
	public List<Map<String, Object>> getSapMoComponentDate38(Map<String, Object> params);
	public List<Map<String, Object>> getSapMoComponentDate39(Map<String, Object> params);
	public List<Map<String, Object>> getSapMoComponentDate40(Map<String, Object> params);
	
	public int insertWmsOutReturnHead(Map<String, Object> params);
	public int insertWmsOutReturnItem(Map<String, Object> params);
	public int insertWmsOutReturnItemDetail(Map<String, Object> params);
	public List<Map<String, Object>> getPlantInfoByWerks(Map<String, Object> params);
	public int getMoHeadHxQty(String AUFNR);
	public int getSapMoCount(Map<String, Object> params);
	public int getSapPoCount(Map<String, Object> params);
	public List<Map<String, Object>> getSapPoStatus(Map<String, Object> params);
	public String getSapMoStatus(Map<String, Object> params);
	
	public List<Map<String, Object>> getPlantLgortInfo(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnOriBatchInfo(Map<String, Object> params);
	public List<Map<String, Object>> getWorkshopReturnOriBatchList(Map<String, Object> params);
	
	public List<Map<String, Object>> getSapWmsMoveType(Map<String, Object> params);
}

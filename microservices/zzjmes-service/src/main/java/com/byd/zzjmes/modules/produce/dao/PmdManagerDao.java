package com.byd.zzjmes.modules.produce.dao;

import java.util.List;
import java.util.Map;

/**
 * 下料明细编辑
 * @author Yangke
 * @date 2019-09-05
 */
public interface PmdManagerDao {
	public List<Map<String, Object>> getPmdList(Map<String, Object> params);
	public List<Map<String, Object>> getPmdMapNoList(Map<String, Object> params);
	public List<Map<String, Object>> getMasterDataMapNoList(Map<String, Object> params);
	public int getPmdListTotalCount(Map<String, Object> params);
	public int editPmdItem(Map<String, Object> params);
	public int delPmdItem(Map<String, Object> params);
	public int addPmdItemHistory(Map<String, Object> params);
	public int addPmdItem(Map<String, Object> params);
	public int delPmdEnc(Map<String, Object> params);
	public int addPmdEnc(List<Map<String, Object>> ecnList);
	
	public List<Map<String, Object>> getProductionExceptionList(Map<String, Object> params);
	public int getProductionExceptionCount(Map<String, Object> params);
	public int exceptionConfirm(Map<String, Object> params);
	
	public int getPmdProcessPlanCount(Map<String, Object> params);
	
	public List<Map<String, Object>> getSubcontractingList(Map<String, Object> params);
	public int getSubcontractingCount(Map<String, Object> params);
	public int editSubcontracting(Map<String, Object> params);

	public List<Map<String, Object>> getSubcontractingHeadList(Map<String, Object> params);
	public int getSubcontractingHeadCount(Map<String, Object> params);

	public List<Map<String, Object>> getSubcontractingItemList(Map<String, Object> params);
	public int editSubcontractingItem(Map<String, Object> params);
}

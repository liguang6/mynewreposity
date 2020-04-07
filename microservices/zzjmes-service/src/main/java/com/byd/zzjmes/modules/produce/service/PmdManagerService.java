package com.byd.zzjmes.modules.produce.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/**
 * 下料明细编辑
 * @author Yangke
 * @date 2019-09-05
 */
public interface PmdManagerService {
	public List<Map<String, Object>> getPmdList(Map<String, Object> params);
	public PageUtils getPmdListPage(Map<String, Object> params);
	public List<Map<String, Object>> getPmdMapNoList(Map<String, Object> params);
	public List<Map<String, Object>> getMasterDataMapNoList(Map<String, Object> params);
	public int editPmdList(Map<String, Object> params);
	public int deletePmdList(Map<String, Object> params);

	public List<Map<String, Object>> getProductionExceptionList(Map<String, Object> params);
	public PageUtils getProductionExceptionPage(Map<String, Object> params);
	public int exceptionConfirm(Map<String, Object> params);
	
	public int getPmdProcessPlanCount(Map<String, Object> params);
	public PageUtils getSubcontractingPage(Map<String, Object> params);
	public List<Map<String, Object>> getSubcontractingList(Map<String, Object> params);
	public int editSubcontractingList(Map<String, Object> params);

	public PageUtils getSubcontractingHeadPage(Map<String, Object> params);
	public List<Map<String, Object>> getSubcontractingItemList(Map<String, Object> params);
	public int editSubcontractingItem(Map<String, Object> params);
}

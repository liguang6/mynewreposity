package com.byd.zzjmes.modules.report.dao;

import java.util.List;
import java.util.Map;

public interface PmdReportDao {

	int getPmdOutputReachCount(Map<String, Object> params);
	List<Map<String,Object>> getPmdOutputReachList(Map<String, Object> params);
	
	int getBatchOutputReachCount(Map<String, Object> params);
	List<Map<String,Object>> getBatchOutputReachList(Map<String, Object> params);
	
	int getPmdReqReachCount(Map<String, Object> params);
	List<Map<String,Object>> getPmdReqReachList(Map<String, Object> params);
	
	int getOrderBatchReachCount(Map<String, Object> params);
	List<Map<String,Object>> getOrderBatchReachList(Map<String, Object> params);
	
	List<Map<String,Object>> getWorkgroupReachList(Map<String, Object> params);
	
	int getOrderAssemblyListCount(Map<String, Object> params);
	List<Map<String,Object>> getOrderAssemblyList(Map<String, Object> params);

	int getPmdProcessTimeCount(Map<String, Object> params);
	List<Map<String,Object>> getPmdProcessTimeList(Map<String, Object> params);
	
	int getPmdProcessTransferTimeCount(Map<String, Object> params);
	List<Map<String,Object>> getPmdProcessTransferTimeList(Map<String, Object> params);
	
}

package com.byd.zzjmes.modules.report.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

public interface PmdReportService {

	public PageUtils pmdOutputReachReport(Map<String, Object> params);
	public List<Map<String,Object>> getPmdOutputReachList(Map<String, Object> params);
	
	public PageUtils batchOutputReachReport(Map<String, Object> params);
	public List<Map<String,Object>> getBatchOutputReachList(Map<String, Object> params);
	
	public PageUtils pmdReqReachReport(Map<String, Object> params);
	
	public PageUtils orderBatchReachReport(Map<String, Object> params);
	
	public List<Map<String,Object>> getWorkgroupReachList(Map<String, Object> params);
	
	public PageUtils orderAssemblyReport(Map<String, Object> params);
	public List<Map<String,Object>> getOrderAssemblyList(Map<String, Object> params);

	public List<Map<String,Object>> getPmdProcessTimeList(Map<String, Object> params);
	public List<Map<String,Object>> getPmdProcessTransferTimeList(Map<String, Object> params);

	public PageUtils pmdProcessTimeReport(Map<String, Object> params);
	public PageUtils pmdProcessTransferTimeReport(Map<String, Object> params);

	
}

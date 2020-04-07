package com.byd.zzjmes.modules.common.service;

import java.util.List;
import java.util.Map;

public interface ZzjCommonService {
	
	public List<Map<String, Object>> getPlanBatchList(Map<String, Object> params);

	public List<Map<String, Object>> getJTProcess(Map<String, Object> params);
	
	public List<Map<String, Object>> getMachineList(Map<String, Object> params);
	
	public List<Map<String, Object>> getAssemblyPositionList(Map<String, Object> params);
	
	public int productionExceptionManage(Map<String, Object> params);
	public List<Map<String, Object>> getProductionExceptionList(Map<String, Object> params);
}

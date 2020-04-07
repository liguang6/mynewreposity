package com.byd.zzjmes.modules.common.dao;

import java.util.List;
import java.util.Map;

public interface ZzjCommonDao {
	
	public List<Map<String, Object>> getPlanBatchList(Map<String, Object> params);

	public List<Map<String, Object>> getJTProcess(Map<String, Object> params);
	
	public List<Map<String, Object>> getMachineList(Map<String, Object> params);
	
	public List<Map<String, Object>> getAssemblyPositionList(Map<String, Object> params);
	
	public int insertProductionException(Map<String, Object> params);
	public int updateProductionException(Map<String, Object> params);
	public int deleteProductionException(Map<String, Object> params);
	public List<Map<String, Object>> getProductionExceptionList(Map<String, Object> params);
}

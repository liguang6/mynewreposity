package com.byd.zzjmes.modules.product.dao;

import java.util.List;
import java.util.Map;

public interface ZzjJTOperationDao {

	List<Map<String, Object>> getJTPlanList(Map<String, Object> params);

	List<Map<String, Object>> getPmdItems(Map<String, Object> params);

	List<String> getJTPlanListByZzj(List<Map<String, Object>> data);

	Map<String, Object> getMachineAchieve(Map<String, Object> params);

	List<String> getJTPlanHeadList(List<Map> head_list);

	void saveJTPlanHead(List<Map> head_list);

	void saveJTPlanItems(List<Map> item_list);

	List<String> getJTOutputListByZzj(List<Map<String, Object>> data);

	void saveZzjOutputData(List<Map> item_list);

	void updateMachinePlanOutput(List<Map> item_list);

	void saveZzjCurrentProcess(List<Map> cp_new_list);

	List<String> getCurrentProcessList(List<Map> item_list);

	void updateZzjCurrentProcess(List<Map> cp_update_list);

	void saveZzjOperRecord(List<Map> item_list);

	int queryOutputRecordsCount(Map<String, Object> params);

	List<Map<String, Object>> queryOutputRecords(Map<String, Object> params);

	Map<String, Object> getPmdPlanOutInfo(Map<String, Object> params);

	void updatePmdOutput(Map<String, Object> params);

	void updateZzjCurrentProcessByItem(Map<String, Object> params);

	void updateMachinePlanOutputByItem(Map<String, Object> params);

	void deletePmdOutput(Map<String, Object> params);
	
	void updateMachinePlanByScrape(Map<String, Object> params);

	int queryCombRecordsCount(Map<String, Object> params);

	List<Map<String, Object>> queryCombRecords(Map<String, Object> params);

	void insertVisualMachinePlan(List<Map> item_list);

	Map<String, Object> getPmdBaseInfo(Map<String, Object> params);

	Map<String, Object> getPmdBindPlan(Map<String, Object> params);

}

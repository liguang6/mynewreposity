package com.byd.qms.modules.processQuality.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface QmsProcessTestDao {
	public List<Map<String, Object>> getProcessTestTplList(Map<String, Object> condMap);
	public List<Map<String,Object>> getFaultList(Map<String, Object> condMap);
	public void updateTestRecord(List<Map<String, Object>> recordList);
	public void updateAbnormal(Map<String, Object> params);
	public void insertTestRecord(List<Map<String, Object>> recordList);
	public int insertAbnormal(Map<String, Object> params);
	public List<Map<String, Object>> getTestRecordInList(Map<String, Object> condMap);
	public int deleteAbnormal(@Param(value="ABNORMAL_ID") int abnormal_id);
	public void updateTestAbnormal(@Param(value="ABNORMAL_ID")int abnormal_id,@Param(value="TEST_RECORD_ID")int record_id);
	public List<Map<String, Object>> getProcessTestRecordList(Map<String, Object> condMap);
	public List<Map<String, Object>> getPartsTestRecordList(Map<String, Object> params);
	public List<Map<String, Object>> getTestNGRecordList(Map<String, Object> params);
	public List<Map<String, Object>> getDPUData(Map<String, Object> params);
	public void updateProcessTestConfirm(List<Map<String, Object>> recordList);
	public List<Map<String, Object>> getFTYData(Map<String, Object> params);
	public List<Map<String, Object>> getNodesTargetVal(Map<String, Object> params);
	public int getPartsTestRecordCount(Map<String, Object> params);
	public int getProcessTestRecordCount(Map<String, Object> params);
	public List<Map<String, Object>> getUnProcessTestList(Map<String, Object> condMap);
	public void deleteUnTestRecord(List<Object> list);
	public void saveUnTestRecord(List<Map<String, Object>> recordList);
	public List<Map<String, Object>> getUnProcessReportData(Map<String, Object> params);
	public List<Map<String, Object>> getBJFtyData(Map<String, Object> params);
	public List<String> getBJFTYOrderList(Map<String, Object> params);
	public List<Map<String, Object>> getFaultScatterData(Map<String, Object> params);
	
}

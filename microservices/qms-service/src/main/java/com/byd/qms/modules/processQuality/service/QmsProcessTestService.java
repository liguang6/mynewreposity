package com.byd.qms.modules.processQuality.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

public interface QmsProcessTestService {
	List<Map<String,Object>> getProcessTestList(Map<String,Object> condMap);

	List<Map<String, Object>> getFaultList(Map<String, Object> params);

	R saveTestRecord(List<Map<String, Object>> recordList);

	R saveAbnormalInfo(Map<String, Object> params);

	R delAbnormalInfo(String abnormal_id, String record_id);

	PageUtils getProcessTestRecordList(Map<String, Object> params);

	PageUtils getProcessTestDetail(Map<String, Object> params);

	PageUtils getPartsTestRecordList(Map<String, Object> params);

	List<Map<String, Object>> getTestNGRecordList(Map<String, Object> params);

	List<Map<String, Object>> getDPUData(Map<String, Object> params);

	R confirmProcessTest(List<Map<String, Object>> recordList);

	R getProcessFTYData(Map<String, Object> params);

	List<Map<String, Object>> getUnProcessTestList(Map<String, Object> params);

	R saveUnTestRecord(List<Map<String, Object>> recordList, String deleteIds);

	R getUnProcessReportData(Map<String, Object> params);

	R getBJFTYData(Map<String, Object> params);

	R getFaultScatterData(Map<String, Object> params);
}

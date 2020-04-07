package com.byd.wms.business.modules.report.dao;

import java.util.List;
import java.util.Map;

import com.byd.utils.R;



public interface EngineMaterialDao {

	List<Map<String, Object>> queryProject(Map<String, Object> params);

	List<Map<String, Object>> getMaterialStock(List<Map<String, Object>> matList);

	void mergeHistory(List<Map<String, Object>> batchpickList);

	void mergeStock(List<Map<String, Object>> stockList);

	List<Map<String, Object>> queryByDay(Map<String, Object> params);

	List<Map<String, Object>> queryBatchpick(List<Map<String, Object>> matList);

	List<Map<String, Object>> queryBitpick(List<Map<String, Object>> matList);

	List<Map<String, Object>> queryAftersale(List<Map<String, Object>> matList);

	List<Map<String, Object>> queryIn(List<Map<String, Object>> matList);

	void updateStock(List<Map<String, Object>> stockList);

	List<Map<String, Object>> queryMat(Map<String, Object> params);



}

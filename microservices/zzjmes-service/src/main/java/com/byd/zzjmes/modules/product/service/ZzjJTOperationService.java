package com.byd.zzjmes.modules.product.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

public interface ZzjJTOperationService {

	List<Map<String, Object>> getJTPlan(Map<String, Object> params);

	R getPmdItems(Map<String, Object> params);

	R getMachineAchieve(Map<String, Object> params);

	R saveJTBindData(Map<String, Object> params);

	R getPmdOutputItems(Map<String, Object> params);

	R saveJTOutputData(Map<String, Object> params);

	R startOpera(Map<String, Object> params);

	PageUtils queryOutputRecords(Map<String, Object> params);

	R getPmdAbleQty(Map<String, Object> params);

	R savePmdOutQty(Map<String, Object> params);

	R deletePmdOutInfo(Map<String, Object> params);

	R scrapePmdOutInfo(Map<String, Object> params);

	PageUtils queryCombRecords(Map<String, Object> params);

	R getPmdBaseInfo(Map<String, Object> params);

	R checkBindPlan(Map<String, Object> params);

}

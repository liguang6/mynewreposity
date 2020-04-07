package com.byd.wms.business.modules.in.service;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.List;
import java.util.Map;

/**
 * STO交货单收货-PDA
 */
public interface WmsSTOReceiptPdaService {
	/**
	 * 获取送货单包装信息数量
	 * @param params
	 * @return
	 */
	R scan(Map<String, Object> params);

	/**
	 * 删除已缓存的STO交货单对应的标签
	 * @param params
	 * @return
	 */
    int deleteLabelCacheInfo(List<String> list);
	/**
	 * 获取已缓存的STO交货单
	 * @param params
	 * @return
	 */
	PageUtils defaultSTOCache(Map<String, Object> params);
	/**
	 * 获取已缓存的STO交货单对应的标签
	 * @param params
	 * @return
	 */
	PageUtils defaultLabelCache(Map<String, Object> params);

	R validateStorage(Map<String, Object> params);

	int deleteAllLabelCache(List<Map<String, Object>> list);

	void calcOpsTime(Map<String, Object> params);

}

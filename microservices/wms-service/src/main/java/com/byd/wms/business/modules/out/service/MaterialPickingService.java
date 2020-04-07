package com.byd.wms.business.modules.out.service;

import java.util.List;
import java.util.Map;

public interface MaterialPickingService {
	/**
	 * 查询出库需求单
	 * @param params
	 * @return 【】
	 */
	public Map<String,Object> selectOutRequirementList(Map<String,Object> param);
	/**
	 * 推荐批次
	 * @param params
	 * @return 【】
	 */
	public Map<String,Object> recommandBatch(Map<String,Object> param);
	/**
	 * 下架
	 * @param params
	 * @return 【】
	 */
	public Map<String,Object> underCarriage(Map<String,Object> param);
	/**
	 * 查询库存批次（用于下架）
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getStockInfo(Map<String,Object> params);
	/**
	 * 校验是否可超需求下架
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getPlantBusinessInfo(Map<String,Object> params);
	/**
	 * 获取仓管员
	 * @param params
	 * @return 【】
	 */
	List<Map<String,Object>> selectWarehourseManager(Map<String, Object> params);
}

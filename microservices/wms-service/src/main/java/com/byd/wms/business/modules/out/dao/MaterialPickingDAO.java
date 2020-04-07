package com.byd.wms.business.modules.out.dao;

import java.util.List;
import java.util.Map;

public interface MaterialPickingDAO {
	
	/**
	 * 查询出库需求单(抬头)
	 * @param params
	 */
	public List<Map<String,Object>> getOutReqHead(Map<String,Object> param);
	/**
	 * 查询出库需求单(行项目)
	 * @param params
	 */
	public List<Map<String,Object>> getOutRequirementList(Map<String,Object> param);
	/**
	 * 批量保存拣配信息(WMS_OUT_PICKING)
	 * @param LIST
	 */
	public int saveOutPicking(List<Map<String,Object>> list);
	/**
	 * 批量更新库存新息
	 * @param LIST
	 */
	public int updateStockInfo(List<Map<String,Object>> list);
	/**
	 * 批量更新需求行项目信息
	 * @param LIST
	 */
	public int updateReqItem(List<Map<String,Object>> list);
	/**
	 * 查询库存（用于拣配下架）
	 **/
	public List<Map<String,Object>> getStockList(List<Map<String,Object>> list);
	
	/**
	 * 查询紧急物料
	 * @param params【工厂代码、有效时间】
	 */
	public List<String> getUrgentMatList(Map<String,Object> params);
	/**
	 * 查询库存批次（用于下架）
	 * @param params
	 */
	public List<Map<String,Object>> getStockInfo(Map<String,Object> params);
	/**
	 * 校验是否可超需求下架
	 */
	public List<Map<String,Object>> getPlantBusinessInfo(Map<String,Object> params);
	/**
	 * 查询仓管员
	 * @param params
	 */
	public List<Map<String,Object>> selectWarehourseManager(Map<String,Object> params);

}

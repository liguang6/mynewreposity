package com.byd.wms.business.modules.in.dao;

import java.util.List;
import java.util.Map;

public interface WmsInPutawayStatusAndLogDao {

	/**
	 * 保存日志
	 * @param params
	 */
	void insertLog(List<Map<String,Object>> params);
	
	/**
	 * 保存状态
	 * @param params
	 */
	void insertStatus(Map<String,Object> params);
	
	/**
	 * 获取一步联动日志
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getLog(Map<String,Object> map);
	
	/**
	 * 更新一步联动状态表
	 * @param params
	 * @return
	 */
	int updatePutawayStatus(List<Map<String,String>> paramss);
	
	/**
	 * 获取一步联动收货信息
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getPutawayInfo(Map<String,Object> map);
	
	/**
	 * 获取一步联动收货日志列表
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getloglist(Map<String,Object> params);
	
	int getloglistCount(Map<String,Object> params);
	
	/**
	 * 再执行更新采购订单号和行项目
	 * @param params
	 * @return
	 */
	int updatePutawayLog(List<Map<String,Object>> params);
	
	/**
	 * 过账成功但SAP凭证未及时返回的
	 * @return
	 */
	List<Map<String,Object>> getSapMatDocEmpty();
	
	/**
	 * 获取凭证已收数量
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> getReceivedQty(List<Map<String, Object>> params);
	
}

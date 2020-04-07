package com.byd.wms.business.modules.in.service;

import java.util.List;
import java.util.Map;

/**
 * 进仓单
 *
 * @author tangjin
 * @email 
 * @date 2018-08-27 10:54:57
 */
public interface WmsInInboundService{
	/**
	 * 查询可进仓的外购收货单清单
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getReceiptList(Map<String,Object> map);
	
	//根据工厂、仓库号、仓管员获取待进仓的来料任务清单
	List<Map<String,Object>> getInboundTasks(Map<String, Object> params);
	
	//根据工厂和仓库号获取物料管理模式-授权码
	List<Map<String,Object>> getRelatedAreaName(Map<String,Object> map);
	public String saveInbound(Map<String, Object> params) throws Exception;
	
	List<Map<String,Object>> getDeptNameByWerk(Map<String,Object> map);
	
	/**
	 * 创建外购进仓单
	 * @param labels 外购物料标签
	 * @return 进仓单号
	 */
	public String newInbound(List<String> labels,String werks,String whNumber,String binCode,String ltWare);
	/**
	 * 查询仓管员关联的 上架任务
	 * @param werks 工厂
	 * @param whNumer 仓管
	 * @param admin 仓管员
	 * @return
	 */
	public List<Map<String,Object>> getInboundTask(String werks,String whNumer,String admin);
	
	//根据收货单号获取收货单信息
	List<Map<String,Object>> getReceiptInfoByReceiveNo(Map<String,Object> map);
	
	//根据采购订单号获取委外组件清单
	List<Map<String, Object>> getComponentInfoByPoNo(Map<String, Object> map);
	
	void insertWmsInPoCptConsume(List<Map<String, Object>> cptList);
	
	List<Map<String,Object>> getBusinessInfo(Map<String,Object> map);
	
}


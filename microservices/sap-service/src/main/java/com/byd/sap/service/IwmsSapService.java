package com.byd.sap.service;

import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import com.byd.sap.modules.job.entity.SapMaterialEntity;
import com.byd.sap.modules.job.entity.SapMaterialUnitEntity;
import com.byd.sap.modules.job.entity.ScheduleJobLogEntity;
import com.byd.sap.modules.job.entity.SapPoAccountEntity;
import com.byd.sap.modules.job.entity.SapPoHeadEntity;
import com.byd.sap.modules.job.entity.SapPoItemEntity;
import com.sap.conn.jco.JCoTable;

public interface IwmsSapService {
	
	/**
	 * 调用SAP IDoc 函数
	 * @param prepareFunName
	 * @param readFunName
	 * @param deleteFunName
	 * @param paraTableName
	 * @param prepareDataMap
	 * @param prepareInputMap
	 * @param readParamMap
	 * @param outParamList
	 * @return
	 */
	public List<JCoTable> getSapJcoData(String prepareFunName, String readFunName, String deleteFunName,
			String paraTableName, List<Map<String,String>> prepareDataList, Map<String, String> prepareInputMap,
			Map<String, String> readParamMap, List<String> outParamList,List<Integer> checkList);
	
	/**
	 * 调用SAP Bapi 函数
	 * @param funName 			函数名
	 * @param paramMap 			参数集
	 * @param outputTableName 	输出表名
	 * @return JCoTable			返回集
	 */
	public JCoTable getSapBapiData(String funName,Map<String,String> paramMap,String outputTableName);
	
	/**
	 * 自动定时任务同步 供应商信息（IDOC方式）
	 * @param output1
	 * @return
	 */
	@Transactional
	public int syncSapVendorData(JCoTable output1);

	/**
	 * 定时任务自动同步 客户信息
	 * @param params
	 * @return
	 */
	public Map<String,Object> syncSapCustomerData(String params);
	
	/**
	 * 自动定时任务同步 生产订单（IDOC方式）
	 * @param output1
	 * @param output2
	 * @param output3
	 * @return
	 */
	@Transactional
	public int syncSapMoData(JCoTable output1,JCoTable output2,JCoTable output3);
	
	/**
	 * 手动同步 生产订单（BAPI方式）
	 * @param queryMap
	 * @return
	 */
	@Transactional
	public int syncSapMoData(Map<String,Object> queryMap);
	
	@Transactional
	public int syncSapMaterialData(SapMaterialEntity sapMaterialEntity);
	
	@Transactional
	public int syncSapMaterialUnitData(List<SapMaterialUnitEntity> sapMaterialUnitEntityList);
	
	public int insert_ScheduleJobLog(ScheduleJobLogEntity scheduleJobLog);
	
	public List<Map<String,String>> getWmsSapCompanyList(Map<String,String> map);
	
	public int syncPoAccount(SapPoAccountEntity sappoaccount);
	
	/**  从SAP系统获取内部订单信息
	 * @param orderId 内部订单号
	 * @return Map ：MESSAGE=返回错误信息（成功则为空），ORDER=订单号，PLANT=工厂，CODE=返回错误代码（成功则为空），STATUS=状态值，
	 * ORDER_NAME=订单描述，ORDER_TYPE=订单类型，COMP_CODE=公司代码
	 */
	public Map<String,String> getSapBapiInternalorderDetail(String orderId);
	
	/**  从SAP系统获取生产订单信息
	 * @param orderId
	 * @return
	 */
	public Map<String,Object> getSapBapiProdordDetail(String orderId);
	
	/**  从SAP系统获取CO订单信息
	 * @param orderId
	 * @return
	 */
	public Map<String,Object> getSapBapiKaufOrder(String orderId);
	
	public Map<String,Object> getSapBapiMaterialInfo(String plant,String material);

	public Map<String,Object> getSapBapiVendorInfo(String vendor);
	
	/**  从SAP系统获取物料凭证信息
	 * @param materialdocument
	 * @param matdocumentyear
	 * @return
	 */
	public Map<String,Object> getSapBapiGoodsmvtDetail(String materialdocument,String matdocumentyear);
	
	/**  从SAP系统获取成本中心信息
	 * @param Costcenter
	 * @return
	 */
	public Map<String,String> getCostcenterDetail(String Costcenter);
	
	/**  从SAP系统获取交货单信息（SAP交货单、STO交货单）
	 * @param orderId
	 * @return
	 */
	public Map<String,Object> getDeliveryGetlist(String deliveryNO);
	
	public String get_SAPPoAccount_id(Map<String,Object> queryMap);
	
	public int update_SapPoAccount(SapPoAccountEntity sappoaccount);
	
	public int syncPoHead(SapPoHeadEntity sappohead);
	
	public int syncPoItem(SapPoItemEntity sappohead);
	
	public int syncPoComponent(Map<String,Object> queryMap);
	/*
	 * bapi方式获取采购订单
	 */
	public boolean getSapBapiPo(String poNo);
	
	/**  从SAP系统获取wbs元素信息
	 * @param wmsno
	 * @return
	 */
	public Map<String,Object> getSapBapiWbs(String wbsno);
	
	/**
	 * 获取SAP库存
	 * @param params
	 * @return
	 */
	public Map<String,Object> queryMaterialStock(Map<String,Object> params);
}

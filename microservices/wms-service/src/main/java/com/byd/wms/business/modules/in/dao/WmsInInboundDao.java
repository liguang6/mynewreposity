package com.byd.wms.business.modules.in.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 进仓单
 * 
 * @author tangjin
 * @email 
 * @date 2018-08-27 10:54:57
 */
public interface WmsInInboundDao{
	
	/**************************************  2019-06 thw  **************************************/
	//根据工厂和仓库号获取物料管理模式-授权码
	List<Map<String,Object>> getRelatedAreaName(Map<String,Object> map);
	
	//查询可进仓的外购收货单信息
	List<Map<String,Object>> getReceiptList(Map<String,Object> map);
	
	//根据授权码查询物料管理模式可进仓的外购收货单信息
	List<Map<String,Object>> queryReceiptByAuthCode(Map<String,Object> map);
	
	//根据库位查询可进仓的外购收货单信息
	List<Map<String,Object>> queryReceiptByLgort(Map<String,Object> map);
	
	//根据收货单号、收货单行项目号获取收货单关联的SAP凭证号、凭证行项目号、凭证年份，及收货单关联的采购订单行项目类别
	List<Map<String,Object>> getWmsDocItemByReceiptNo(@Param(value="condList")List<Map<String,Object>> condList);
	
	public void insertWmsInboundItem(List<Map<String, Object>> itemList);
	public void insertWmsInboundHead(Map<String, Object> head);
	
	//进仓交接，保存委外PO消耗物料清单
	void insertWmsInPoCptConsume(List<Map<String, Object>> cptList);
	
	//根据交货单号获取交货单信息
	List<Map<String,Object>> getReceiptInfoByReceiveNo(Map<String,Object> map);
	
	//根据采购订单号获取委外组件信息
	List<Map<String,Object>> getComponentInfoByPoNo(Map<String,Object> map);
	
	/**
	 * 查询人料关系
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> queryMatManager(Map<String,Object> params);
	
	/**
	 * 查询出库上架任务
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> queryWhTask(Map<String,Object> params);
	
	/**
	 * 根据标签字符串如 L001,L002查询标签详细信息
	 */
	List<Map<String,Object>> queryLabel(Map<String,Object> params);
	
	List<Map<String,Object>> getBusinessInfo(Map<String,Object> map);
	
	//根据进仓单、进仓单行项目，或者收货单、收货单行项目查询标签订单
	List<Map<String,Object>> getLabelList(Map<String,Object> map);
	
	/**************************************  2019-06 thw  **************************************/
	

   //获取物料物流参数配置表的满箱数量
   List<Map<String,Object>> getWmsCMatLtSample(Map<String,Object> map);
   
   List<Map<String,Object>> getWmsCMatPackageHead(Map<String,Object> map);
   //获取仓管员
   List<Map<String,Object>> getWhManager(Map<String,Object> map);
   
   List<Map<String,Object>> getBusinessInfo_cond(Map<String,Object> map);
   
   Map<String,Object> getEntryQty(Map<String,Object> map);
   
   List<Map<String,Object>> getInBoundHead(Map<String,Object> map);
   
   List<Map<String,Object>> getInBoundItem(Map<String,Object> map);
   
   List<Map<String,Object>> getHxInfo(Map<String,Object> map);
   
   int updateRHStock(Map<String, Object> params);
   
   int updateReceipt(Map<String, Object> params);
   
   int updateInboundItemRealQty(Map<String, Object> params);
   
   int updateInboundItemStatus(Map<String, Object> params);
   
   int updateInboundHeadStatus(Map<String, Object> params);
   
   int updateInboundItemLabel(Map<String, Object> params);
   
   List<Map<String,Object>> getPOITEM(Map<String, Object> params);
   
   List<Map<String,Object>> getReceiptByReceiptNo(Map<String, Object> params);
   
   List<Map<String,Object>> getSAPMaterialUnit(Map<String, Object> params);
   
   List<Map<String,Object>> getItemLs(Map<String, Object> params);
   
   List<Map<String,Object>> getItemReceipt(Map<String, Object> params);
   
   int updateCoreLabel(List<Map<String, Object>> params);
   
   List<Map<String,Object>> getHxMOInfo(Map<String,Object> map);
   
   List<Map<String,Object>> getMOHEADInfo(Map<String,Object> map);
   
   List<Map<String,Object>> getMOListInfo(Map<String,Object> map);
   
   List<Map<String,Object>> getGZQty(Map<String,Object> map);
   
   List<Map<String,Object>> getJCQty(Map<String,Object> map);
   
   List<Map<String,Object>> getMOCOMPListInfo(Map<String,Object> map);
   
   List<Map<String,Object>> getWmsBusinessClass(Map<String,Object> map);
   
   List<Map<String,Object>> getHXMOITEM(Map<String,Object> map);
   
   int updateHXMOITEM(List<Map<String, Object>> params);
   
   List<Map<String,Object>> getHXMOCOMPONENT(Map<String,Object> map);
   
   List<Map<String,Object>> getMOComponentListByhx(Map<String,Object> map);
   
   int updateHXMOCOMP(List<Map<String, Object>> params);
   
   //根据进仓单行项目及收货单行项目获取关联的标签信息，并校验关联标签是否已创建进仓单
   List<Map<String,Object>> getInboundItemLabelList(@Param(value="condList")List<Map<String,Object>> condList);
   
   List<Map<String,Object>> getLabelQtyByReceiveNo(Map<String,Object> map);
   
   List<Map<String,Object>> getLabelQtyByReceiveNoAndItemNo(Map<String,Object> map);//
   
   List<Map<String,Object>> getLabelQtyByInboundNoAndItemNo(Map<String,Object> map);
   
   List<Map<String,Object>> getReceiptQtyByReceiveNoAndItemNo(Map<String,Object> map);
   
   List<Map<String,Object>> geInboundQtyByReceiveNoAndItemNo(Map<String,Object> map);//
   
   List<Map<String,Object>> geInboundQtyByInboundNoAndItemNo(Map<String,Object> map);
   
   List<Map<String,Object>> getLabelQtyByInboundNo(Map<String,Object> map);//
   /**
    * 获取business_name
    * @param map
    * @return
    */
   List<Map<String,Object>> getBusinessNameByReceiveNo(Map<String,Object> map);
   /**
    * 获取business_name
    * @param map
    * @return
    */
   List<Map<String,Object>> getBusinessNameByInboundNo(Map<String,Object> map);
   
   int updateLabelQtyAndStatus(Map<String, Object> params);
   
   int updateHXPO(Map<String, Object> params);
   
   List<Map<String,Object>> getMOITEMMaktx(Map<String,Object> map);
   
   List<Map<String,Object>> getWMSDOCMatDoc(Map<String,Object> map);
   
   List<Map<String,Object>> getSAPDOCDocDate(Map<String,Object> map);
   
   List<Map<String,Object>> getDeptNameByWerk(Map<String,Object> map);
   
   List<Map<String,Object>> getCompNameByWerk(Map<String,Object> map);
   
   List<Map<String,Object>> getWMSDOCINFOByInBoundNo(Map<String,Object> map);
   
   /**
	 *  获取采购订单行项目列表
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getPOItemsByPoNO(Map<String, Object> params);
	/**
	 * wms_in_inbound_item进仓单表进仓数量
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getGZQtyByPONo(Map<String, Object> params);
	
	List<Map<String, Object>> getGZQtyByPONoGuanbi(Map<String, Object> params);
	
	List<Map<String,Object>> getItemLsByPONO(Map<String, Object> params);
	/**
	 * WMS_IN_RECEIPT收货单表收货数量
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getReceiveQtyByPONo(Map<String, Object> params);
	
	List<Map<String, Object>> getItemLsByLabelNo(Map<String, Object> params);
	
	int updateLabelStatusByLabelNo(Map<String, Object> params);
	
	List<Map<String,Object>> getManagerByMaterial(Map<String,Object> map);
	
	List<Map<String,Object>> getManagerByLgort(Map<String,Object> map);
	
	void insertWmsCoreStockLabel(List<Map<String, Object>> itemList);
	/**
	 * 仓库配置
	 */
	List<Map<String,Object>> queryCWh(Map<String,Object> params);

	/**
	 * 进仓单
	 */
	List<Map<String,Object>> queryInInbound(Map<String,Object> params);
	
	/**
	 * 查询仓库储位排序
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> queryWhBinSQE(Map<String,Object> params);
	
	/**
	 * 更新仓库标签
	 * @param params
	 */
	void updateStockLabelByLabelNo(Map<String,Object> params);
	
	/**
	 * 更新仓库任务
	 */
	void updateWhTaskById(Map<String,Object> params);
	
	/**
	 * 查询进仓单行项目
	 */
	List<Map<String,Object>> queryInInboundItem(Map<String,Object> params);
	
	/**
	 * 根据最新移储储位
	 * @param batch
	 * @return
	 */
	String queryLatestMoveStoreageByBatch(String batch);
	
	/**
	 * 查询进仓单的，库位
	 * @param werks
	 * @param whNumber
	 * @return
	 */
	List<Map<String,Object>> queryLogrtFromInInbound(String werks,String whNumber);
	int delStockLabelByLabelNo(Map<String,Object> map);
	List<Map<String, Object>> getLabelInfo(Map<String, Object> params);
	
	int updateReceiptDestroyQty(Map<String,Object> params);
	
	List<Map<String,Object>> getInpoCptConsume(List<Map<String, Object>> cptList);
	//
	List<Map<String,Object>> getInboundHeadList(List<Map<String, Object>> inboundheadList);
	
	int updateReceiptDestroyQtyByList(List<Map<String, Object>> params);
	
	int updateRHStockByList(List<Map<String, Object>> params);
	
	int updateReceiptByList(List<Map<String, Object>> params);
	
	int updateInboundItemRealQtyByList(List<Map<String, Object>> params);
	
	int updateInboundItemStatusByList(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getItemReceiptByList(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getReceiptByReceiptNoByList(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getPOITEMByList(List<Map<String, Object>> params);
	
	int updateHXPOByList(List<Map<String, Object>> params);
	
	int updateLabelQtyAndStatusByList(List<Map<String, Object>> params);
	
	int updateLabelStatusByLabelNoByList(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getWmsCoreWhBinByList(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getKnttp(Map<String, Object> params);
	
	List<Map<String,Object>> getMoveAndSynByAll(Map<String, Object> params);
	
	int updateInboundHeadStatusByList(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getRhStockList(List<Map<String, Object>> params);
	
	int deleteRHZeroStock(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getInBoundItemAllSt(Map<String,Object> map);
	
	List<Map<String,Object>> getLabelInfoBy303Z23(List<Map<String, Object>> params);
	
	int updateLabelInboundByList(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getMOCOMPListInfoBylist(List<Map<String, Object>> params);
	
	List<Map<String,Object>> getKnttpByInternal(Map<String, Object> params);
	
}

package com.byd.wms.business.modules.out.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SendCreateRequirementDao {
  public List<Map<String,Object>> selectPlantBusinessTypes(@Param("params") Map<String,Object> params);
  public List<Map<String,Object>> selectWMSSapMoHead(@Param("params") Map<String,Object> params);
  public List<Map<String,Object>> selectHxMoComponents(@Param("params") Map<String,Object> params);
  public List<Map<String,Object>> selectMaterial(@Param("params") Map<String,Object> params);
  public List<Map<String,Object>> selectLgortStockByMaterial(@Param("params") Map<String,Object> params);
  
  /**
   * 查询生产订单信息
   * @param params
   * @return
   */
  public List<Map<String,Object>> selectProducerOrder(@Param("params") Map<String,Object> params);


  
  public Double selectTotalStockQty(@Param("params") Map<String,Object> params);
  
  public List<Map<String,Object>> selectHxFlag(@Param("params") Map<String,Object> params);
  
  /**
   * 查询非上线物料
   * @param werks
   * @return
   */
  public List<Map<String,Object>> selectMatUsing(String werks);
  
  public List selectBusinessCode(@Param("type")String type,@Param("name")String name);

  /**
   * 查询审批标识
   * @param params
   * @return
   */
  public List<Map<String,Object>> selectApprovalFlag(@Param("params")Map<String,Object> params);

  /**
   * 委外订单出库-查询SAP外购订单
   * @param params
   * @return
   */
  public List<Map<String,Object>> selectSapPoHead(@Param("params")Map<String,Object> params);

  /**
   * 获取委外订单出库信息
   * @param params
   * @return
   */
  public List<Map<String,Object>> selectOutReqItems6(@Param("params")Map<String,Object> params);

  /**
   * SAP交货单核销信息
   * @param params
   * @return
   */
  public List<Map<String,Object>> selectHxDn(@Param("params")Map<String,Object> params);
  /**
   * UB转储类型，已领数量查询
   * @param poNo
   * @param matnr
   * @return
   */
  public Double selectHasReceivedQty(@Param("business_name")String busName,@Param("PO_NO")String poNo,@Param("MATNR")String matnr,@Param("poItemNo") String poItemNo);
  public Double selectAvaliableQty(@Param("business_name")String busName,@Param("PO_NO")String poNo,@Param("MATNR")String matnr);

  /**
   * 生产订单算已领数量
   * @param busName
   * @param poNo
   * @param matnr
   * @param poItemNo
   * @return
   */
  public Double selectHasReceivedQty2(@Param("business_name")String busName,@Param("MO_NO")String poNo,@Param("MATNR")String matnr,@Param("moItemNo") String poItemNo);

  /**
   * 查询业务代码
   * @param businessName
   * @param businessType
   * @param businessClass
   * @return
   */
  public List<Map<String,Object>> selectApprovalBusinessCode(@Param("business_name")String businessName,@Param("business_type")String businessType,@Param("business_class")String businessClass);

  /**
   * 查询sap生产订单信息
   * @param werks
   * @param poNO
   * @return
   */
  public List<Map<String,Object>> selectSapPoHeadInfo(@Param("WERKS")String werks,@Param("EBELN")String poNO);

  /**
   * 
   * @param werks 接收工厂
   * @param fWerks 发货工厂
   * @param matnr 物料号
   * @return
   */
  public List<Map<String,Object>> selectMatnrHx(@Param("werks")String werks,@Param("f_werks")String fWerks,@Param("matnr")String matnr);



  public List<Map<String,Object>> selectPlantHx(@Param("WERKS")String WERKS,@Param("WH_NUMBER")String WH_NUMBER);


  /**
   * 核销虚发数量
   * @param werks
   * @param fwerks
   * @param matnr
   * @return
   */
  public List<Map<String,Object>> selectHxQtyXf(@Param("werks")String werks,@Param("fwerks")String fwerks,@Param("SAP_MATDOC_NO")String SAP_MATDOC_NO,@Param("matnr")String matnr,@Param("onlyOverZero")String onlyOverZero);

  /**
   * WMS_HX_DN SAP交货单核销信息
   * @param werks
   * @param vbeln
   * @return
   */
  public List<Map<String,Object>> selectWmsHxDn(@Param("WERKS")String werks,@Param("VBELN")String vbeln,@Param("xf")String xf);

  /**
   * 标签
   * @param params
   * @return
   */
  public List<Map<String,Object>> selectCoreLabel(Map<String,Object> params);
  
  public List<Map<String,Object>> queryOutReqPda311(Map<String,Object> params);
  public List<Map<String,Object>> queryUNIT(Map<String,Object> params);
  
  /**
   * 总装配送验证配送单
   * @param distribution
   * @param deliveryType
   * @return
   */
  public List<Map<String,Object>> selectCallMaterial(@Param("distribution")String distribution,@Param("deliveryType")String deliveryType);

  /**
   * 总装配送验证配送单
   */
  public Map<String,String> selectPOPro(@Param("params") Map<String,String> params);
}

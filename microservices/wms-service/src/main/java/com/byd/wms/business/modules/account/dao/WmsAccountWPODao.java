package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.account.entity.WmsHxToEntity;

/**
 * 无PO收货账务处理 service
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-11-22 16:06:38
 */
public interface WmsAccountWPODao {
	
	/**
	 * 根据工厂、仓库库、供应商代码、收货日期查询无PO收货产生的收货单信息
	 * @param params WERKS：工厂  WH_NUMBER：仓库号  LIFNR：供应商代码  收货日期：CREATE_DATE_S-CREATE_DATE_E
	 * BEDNR：需求跟踪号
	 * @return 无PO收货单信息
	 */
    List<Map<String, Object>> getWPOReceiptInfo(Map<String,Object> params);
    
    /**
     * 根据采购订单号获取采购订单行项目信息（排查已删除、交货已完成的行项目）
     * @param params PO_NO：采购订单号
     * @return 采购订单行项目信息
     */
    List<Map<String, Object>> getPoItemInfo(Map<String,Object> params);
    
    /**
     * 更新无PO收料产生的收货单的采购订单、行项目号，将收货单行项目的WMS业务类型由05 无PO->0502 无PO-补PO
     * @param receiptInfoList
     * @return
     */
    int updateReceiptPoInfo(List<Map<String,Object>> receiptInfoList);
    
    /**
     * 更新无PO收料产生的103W、105W wms凭证的采购订单、行项目号
     * @param receiptInfoList
     * @return
     */
    int updateWmsDocPoInfo(List<Map<String,Object>> receiptInfoList);
    
    /**
     * 将凭证行项目的WMS业务类型由05 无PO->0502 无PO-补PO
     * @param receiptInfoList
     * @return
     */
    int updateWmsDocBusinessType(List<Map<String,Object>> receiptInfoList);
    
    /**
     * 更新进仓单行项目，将行项目状态为(00 已创建 01 部分进仓)的进仓单行项目的WMS业务类型为 由 05 无PO->0502 无PO-补PO
     * @param receiptInfoList
     * @return
     */
    int updateInboundItemBusinessType(List<Map<String,Object>> receiptInfoList);
    
    /**
     * 更新凭证行项目的SAP过账数量和SAP移动类型
     * @param receiptInfoList
     * @return
     */
    int updateWmsDocSapMoveType(List<Map<String,Object>> receiptInfoList);
    
    /**
     * 根据收货单及行项目获取105W wms物料凭证信息
     * @param params RECEIPT_NO：收货单号 RECEIPT_ITEM_NO：收货单行项目号
     * @return wms物料凭证信息
     */
    List<Map<String, Object>> getWpoWmsDocInfo(List<Map<String,Object>> params);
}

package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.account.entity.WmsHxToEntity;

/**
 * 跨工厂收货账务处理 dao
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-12-05 16:06:38
 */
public interface WmsAccountKPODao {
	
	/**
	 * 根据工厂、仓库库、采购订单、料号、供应商代码、收货进仓日期查询跨工厂收货进仓产生的105DS WMS凭证信息
	 * @param params PO_WERKS： 采购订单工厂   WERKS：收货工厂  WH_NUMBER：收货仓库号  LIFNR：供应商代码  收货进仓日期：CREATE_DATE_S-CREATE_DATE_E
	 * BEDNR：需求跟踪号  PO_NO：采购订单： 料号：MATNR 
	 * @return 跨工厂收货进仓产生的105DS WMS凭证信息
	 */
    List<Map<String, Object>> getKPOWmsDocInfo(Map<String,Object> params);
    
    /**
     * 更新凭证行项目的SAP过账数量
     * @param receiptInfoList
     * @return
     */
    int updateWmsDocSapMoveType(List<Map<String,Object>> receiptInfoList);
    
    /**
     * 更新WMS凭证行项目的可冲销、可取消 标识
     * @param matList
     * @return
     */
    int updateWmsDocCancelFlag(List<Map<String,Object>> matList);
    
}

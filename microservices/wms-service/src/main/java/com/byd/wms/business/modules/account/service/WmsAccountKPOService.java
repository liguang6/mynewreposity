package com.byd.wms.business.modules.account.service;

import java.util.List;
import java.util.Map;
import com.byd.utils.R;

/**
 * 跨工厂收货账务处理Service
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-12-05 16:06:38
 */
public interface WmsAccountKPOService{
    
	/**
	 * 根据工厂、仓库库、采购订单、料号、供应商代码、收货进仓日期查询跨工厂收货进仓产生的105DS WMS凭证信息
	 * @param params PO_WERKS： 采购订单工厂   WERKS：收货工厂  WH_NUMBER：收货仓库号  LIFNR：供应商代码  收货进仓日期：CREATE_DATE_S-CREATE_DATE_E
	 * BEDNR：需求跟踪号  PO_NO：采购订单： 料号：MATNR 
	 * @return 跨工厂收货进仓产生的105DS WMS凭证信息
	 */
    List<Map<String, Object>> getKPOWmsDocInfo(Map<String,Object> params);
    
	/**
	 * 跨工厂收货账务处理-101收货
	 * @param params
	 * @return
	 */
	R postGR_101(Map<String, Object> params);
}


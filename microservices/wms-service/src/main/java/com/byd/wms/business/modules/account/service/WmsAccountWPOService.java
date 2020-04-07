package com.byd.wms.business.modules.account.service;

import java.util.List;
import java.util.Map;
import com.byd.utils.R;

/**
 * 无PO收货账务处理Service
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-11-20 16:06:38
 */
public interface WmsAccountWPOService{
    
	/**
	 * 根据工厂、仓库库、供应商代码、收货日期查询无PO收货产生的收货单信息
	 * @param params WERKS：工厂  WH_NUMBER：仓库号  LIFNR：供应商代码  收货日期：CREATE_DATE_S-CREATE_DATE_E
	 * BEDNR：需求跟踪号
	 * @return 无PO收货单信息
	 */
    List<Map<String, Object>> getWPOReceiptInfo(Map<String,Object> params);
    
    List<Map<String, Object>> getPoItemInfo(Map<String,Object> params);
    
	/**
	 * 无PO收货账务处理
	 * @param params
	 * @return
	 */
	R postGI_WPO(Map<String, Object> params);
}


package com.byd.wms.business.modules.account.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.entity.WmsHxPoEntity;
import java.util.List;
import java.util.Map;

/**
 * SAP采购订单 核销业务Service
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-20 16:06:38
 */
public interface WmsAccountReceiptHxPoService extends IService<WmsHxPoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    /**
     * 根据采购订单号+采购订单行项目号更新PO核销信息
     * @param wmsHxPoEntity 
     * @return boolean true/false
     */
    public boolean updateByPoNo(WmsHxPoEntity wmsHxPoEntity);
    
    /**
     * 根据工厂、供应商查询危化品物料清单
     * @param params Map WERKS：工厂代码  LIFNR：供应商代码
     * @return List
     * MATNR：料号 GOOD_DATES：保质期
     */
    List<Map<String, Object>> getDangerMatList(Map<String, Object> params);
	/**
	 * 根据供应商代码，工厂代码获取采购和供应商简称（判断WMS_C_PLANT表是否配置了工厂启用供应商管理）
	 */
	Map<String, Object> getVendorInfo(Map<String, Object> params);
	/**
	 * 获取采购订单行项目列表-核销
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getPoItems(Map<String, Object> params);
	/**
	 * PO采购订单确认收货(V)-核销
	 * @param params
	 * @param r
	 */
	R boundIn_POV(Map<String, Object> params);
	
	String setMatBatch(Map<String, Object> params,List<Map<String,Object>> matList);
}


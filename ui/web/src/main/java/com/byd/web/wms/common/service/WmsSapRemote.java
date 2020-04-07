package com.byd.web.wms.common.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "SAP-SERVICE")
public interface WmsSapRemote {
	
	/**
	 * 从SAP系统获取物料凭证信息
	 * @param materialdocument
	 * @param matdocumentyear
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiGoodsmvtDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiGoodsmvtDetail(@RequestParam(value = "materialdocument") String order_id,@RequestParam(value = "matdocumentyear") String year);
	
	/**
	 * 从SAP系统获取成本中心信息
	 * @param Costcenter
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiCostcenterDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiCostcenterDetail(@RequestParam(value = "Costcenter") String Costcenter);
	
	/**
	 * 从SAP系统获取内部订单信息
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiInternalorderDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiInternalorderDetail(@RequestParam(value = "orderId") String orderId);
	
	/**
	 * 从SAP系统获取交货单信息（SAP交货单、STO交货单）
	 * @param deliveryNO
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiDeliveryGetlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiDeliveryGetlist(@RequestParam(value = "deliveryNO") String deliveryNO);
	
	/**
	 * 从SAP系统获取WBS元素信息
	 * @param wbsno
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiWbs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiSapBapiWbs(@RequestParam(value = "wbsno") String wbsno);
	
	/**
	 * 从SAP系统获取CO订单信息
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiKaufOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiKaufOrder(@RequestParam(value = "orderId") String orderId);

	/**
	 * 从SAP系统获取生产订单信息
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiProdordDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiProdordDetail(@RequestParam(value = "orderId") String orderId);
	
	/**
	 * 手动同步 生产订单 从SAP系统获取生产订单信息
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiProdordDetailSync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiProdordDetailSync(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	/**
	 * 手动同步 生产订单 从SAP系统获取采购订单信息
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiPoDetailSync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiPoDetailSync(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	/**
	 * 手动同步 物料 从SAP系统获取物料信息
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiMaterialInfoSync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiMaterialInfoSync(@RequestParam(value = "paramMap") Map<String,Object> paramMap);

	/**
	 * 手动同步 供应商 SAP供应商数据信息
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiVendorInfoSync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapBapiVendorInfoSync(@RequestParam(value = "paramMap") Map<String,Object> paramMap);

	/**
	 * 货物移动过账 
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiGoodsmvtCreate", method = RequestMethod.POST)
	Map<String,Object> getSapBapiGoodsmvtCreate(@RequestBody Map<String,Object> paramMap);


	/**
	 * 冲销货物移动凭证
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapBapiGoodsmvtCancel", method = RequestMethod.POST)
	Map<String,Object> getSapBapiGoodsmvtCancel(@RequestBody Map<String,Object> paramMap);
	
	/**
	 * SAP交货单简配、修改
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapDeliveryChange", method = RequestMethod.POST)
	Map<String,Object> getSapDeliveryChange(@RequestBody Map<String,Object> paramMap);
	
	/**
	 * SAP交货单过账
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapDeliveryUpdate", method = RequestMethod.POST)
	Map<String,Object> getSapDeliveryUpdate(@RequestBody Map<String,Object> paramMap);
	
	/**
	 * 定时任务
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapMaterialSync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapMaterialSync(@RequestParam(value = "params") String params);

	/**
	 * 定时任务
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapVendorSync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapVendorSync(@RequestParam(value = "params") String params);
	
	/**
	 * 定时任务
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapMoSync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapMoSync(@RequestParam(value = "params") String params);

	/**
	 * 定时任务
	 * @return
	 */
	@RequestMapping(value = "/sap-service/SapPOSync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String,Object> getSapPOSync(@RequestParam(value = "params") String params);
}

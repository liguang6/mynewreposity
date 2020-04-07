package com.byd.web.wms.account.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 无PO收货账务，采购补下采购订单后，对应的WMS凭证过账到SAP
 * 处理逻辑：
 * 1 查询无PO收料产生的收货单，将采购补下的PO及行项目号（校验：采购订单行项目未收货数量>=收货单数量）维护到对应的收货单行项目
 * 2 更新收货单关联的103W、105W WMS凭证对应的PO及行项目号，
 * 3 已产生的105W WMS凭证执行SAP过账
 * 4 收货单及关联的103W凭证的BUSINESS_TYPE-WMS业务类型由无PO更新为无PO-补PO：解决部分物料还在质检尚未进仓，后续进仓过账问题
 * @author (changsha) thw
 * @date 2018-11-19
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsAccountWPORemote {
    
    /**
     * 查询无PO收料产生的收货单
     * @param params
     * @return
     */
	@RequestMapping(value = "/wms-service/account/wmsAccountWPO/listWPOMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R listWPOMat(@RequestBody Map<String, Object> params) ;
	
    /**
     * 查询采购订单信息
     * @param params
     * @return
     */
	@RequestMapping(value = "/wms-service/account/wmsAccountWPO/getPoItemInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getPoItemInfo(@RequestParam(value="params") Map<String, Object> params) ;
	
	
	
	
    /**
     * 无PO收货账务处理
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/account/wmsAccountWPO/postGI", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R postGI(@RequestParam(value="params") Map<String, Object> params) ;
}

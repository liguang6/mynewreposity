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
 * 账务处理-发货V（核销业务虚发）
 * 包含：SAP采购订单收料(V)、工厂间调拨收料（V）、SAP交货单收料（V)、成品入库（V）
 * @author (changsha) thw
 * @date 2018-11-01
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsAccountOutVRemote {

	@RequestMapping(value = "/wms-service/account/wmsAccountOutV/listMOVMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R listMOVMat(@RequestBody Map<String, Object> params);
	
	
    /**
     * 发货（V）过账-核销业务
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/account/wmsAccountOutV/postGI", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R postGI(@RequestBody Map<String, Object> params);
    
    /**
     * 根据料号、工厂、仓库号获取物料信息、库存信息、行文本、头文本
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/account/wmsAccountOutV/getMatInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getMatInfo(@RequestParam(value="params") Map<String, Object> params);
    
    /**
     * STO虚发物料数据获取
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/account/wmsAccountOutV/getSTOVMatList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getSTOVMatList(@RequestBody Map<String, Object> params);
}

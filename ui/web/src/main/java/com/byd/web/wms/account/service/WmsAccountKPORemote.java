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
 * 跨工厂收货账务处理
 * 跨工厂收货，质检合格入库后，采购工厂做收货账务（101），再检查STO定价是否有效，由收货工厂做STO业务的收货联动账务
 * @author (changsha) thw
 * @date 2018-11-19
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsAccountKPORemote {
	@RequestMapping(value = "/wms-service/account/wmsAccountKPO/listKPOMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R listKOVMat(@RequestBody Map<String, Object> params);
	
	
    /**
     * 跨工厂收货（101）账务处理
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/account/wmsAccountKPO/postGR", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R postGR(@RequestBody Map<String, Object> params);
}

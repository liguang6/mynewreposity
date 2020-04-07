package com.byd.web.wms.query.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author rain
 * @version 创建时间：2019年11月25日09:12:49
 * @description 云平台送货单查询
 */
@FeignClient(name = "WMS-SERVICE")
public interface YPTDeliveryQueryRemote {
	
    @RequestMapping(value = "/wms-service/query/yptquery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R list(@RequestParam(value = "params") Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/query/yptquery/listitem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R listitem(@RequestParam(value = "params") Map<String, Object> params);

    @RequestMapping(value = "/wms-service/query/yptquery/yptdeliverbarlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R yptdeliverbarlist(@RequestParam(value = "params") Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/query/yptquery/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R update(@RequestParam(value = "params") Map<String, Object> params);
}

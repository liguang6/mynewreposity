package com.byd.wms.webservice.common.remote;

import com.byd.wms.webservice.common.fallback.BusinessRemoteFallBackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * @auther: peng.yang8
 * @data: 2019/12/20
 * @description：
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCallMaterialsServiceRemote {

    @RequestMapping(value = "/wms-service/webservices/storeorder/callMaterials", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String callMaterials(@RequestBody String params);
}

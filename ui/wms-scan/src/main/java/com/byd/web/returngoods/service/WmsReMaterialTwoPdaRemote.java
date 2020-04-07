package com.byd.web.returngoods.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="WMS-SERVICE")
public interface WmsReMaterialTwoPdaRemote {
    @RequestMapping  (value="/wms-service/returnpda/wmsRematerialTwopda/scanTwo",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getWmsDocByLabelNo(@RequestParam(value = "params") Map<String, Object> params);

    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialTwopda/scanInfoTwo",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
    R getScanInfo(@RequestParam(value = "params") Map<String, Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialTwopda/removeScanInfoTwo",method = RequestMethod.POST,produces =MediaType.APPLICATION_JSON_VALUE)
    R removeScanInfo(@RequestParam(value = "params") Map<String, Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialTwopda/confirmWorkshopReturnTwo",method = RequestMethod.POST,produces =MediaType.APPLICATION_JSON_VALUE)
    R confirmWorkshopReturn(@RequestParam(value = "params") Map<String, Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialTwopda/valiateLabelTwo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    R validateLabelTwo(@RequestParam(value = "params") Map<String ,Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialTwopda/saveData",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    R saveData(@RequestParam(value = "params") Map<String,Object> params);
}

package com.byd.web.returngoods.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="WMS-SERVICE")
public interface WmsReMaterialPdaRemote {
    @RequestMapping  (value="/wms-service/returnpda/wmsRematerialpda/scan",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getWmsDocByLabelNo(@RequestParam(value = "params") Map<String, Object> params);

    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialpda/scanInfo",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
    R getScanInfo(@RequestParam(value = "params") Map<String,Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialpda/removeScanInfo",method = RequestMethod.POST,produces =MediaType.APPLICATION_JSON_VALUE)
    R removeScanInfo(@RequestParam(value = "params") Map<String,Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialpda/confirmWorkshopReturn",method = RequestMethod.POST,produces =MediaType.APPLICATION_JSON_VALUE)
    R confirmWorkshopReturn(@RequestParam(value="params") Map<String,Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialpda/valiateLabel",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    R validateLabel(@RequestParam(value = "params") Map<String ,Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialpda/initialize",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    R initialize(@RequestParam(value = "params") Map<String ,Object> params);
    @RequestMapping(value = "/wms-service/returnpda/wmsRematerialpda/saveData",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    R saveData(@RequestParam(value = "params") Map<String,Object> params);
}

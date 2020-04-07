package com.byd.web.wms.in.service;


import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FeignClient(name = "WMS-SERVICE")
public interface StoPrintConfigRemote {
    @RequestMapping(value = "/wms-service/in/sto/query",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public R query(@RequestParam(value="params") Map<String,Object> params);
    @RequestMapping(value = "/wms-service/in/sto/edit",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public R edit(@RequestParam(value="params") Map<String,Object> params);
    @RequestMapping(value = "/wms-service/in/sto/add",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public R add(@RequestParam(value="params")Map<String,Object>params);
    @RequestMapping(value = "/wms-service/in/sto/del",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public R del(@RequestParam(value = "params") Map<String,Object> parmas);

}



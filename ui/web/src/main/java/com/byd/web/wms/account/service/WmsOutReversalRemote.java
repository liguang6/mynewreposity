package com.byd.web.wms.account.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "WMS-SERVICE")
public interface WmsOutReversalRemote {
/*
    @RequestMapping(value = "/wms-service/account/wmsOutReversal/getOutReversalData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getOutReversalData(@RequestParam Map<String, Object> params) ;*/

    @RequestMapping(value = "/wms-service/account/wmsOutReversal/confirmOutReversalData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R confirmOutReversalData(@RequestParam Map<String, Object> params) ;

    @RequestMapping(value = "/wms-service/account/wmsOutReversal/confirmOutCancelData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R confirmOutCancelData(@RequestParam Map<String, Object> params);
}

package com.byd.web.in.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsAsnReceiptRemote {

	@RequestMapping(value = "/wms-service/in/asnReceipt/scan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R scan(@RequestBody Map<String, Object> params);
}

package com.byd.web.bjmes.common.service;

import java.util.Map;

import com.byd.utils.R;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "BJMES-SERVICE")
public interface BjCommonRemote {

	@RequestMapping(value = "/bjmes-service/common/getOrderList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
     R getOrderList(@RequestBody Map<String, Object> paramMap);
    
}
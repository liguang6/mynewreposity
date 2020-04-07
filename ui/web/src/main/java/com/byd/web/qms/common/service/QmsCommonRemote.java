package com.byd.web.qms.common.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

@FeignClient(name = "QMS-SERVICE")
public interface QmsCommonRemote {
	
    @RequestMapping(value = "/qms-service/common/getBusTypeCodeList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getBusTypeCodeList(@RequestParam(value = "busTypeCode") String busTypeCode);
    
    @RequestMapping(value = "/qms-service/common/getTestNodes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getTestNodes(@RequestParam(value = "testType") String testType, @RequestParam(value = "TEST_CLASS") String TEST_CLASS);
    
    @RequestMapping(value = "/qms-service/common/getOrderNoList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getOrderNoList(@RequestParam(value = "orderNo") String orderNo);

    @RequestMapping(value = "/qms-service/common/getBusList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getBusList(@RequestBody Map<String,Object> condMap);
    
    @RequestMapping(value = "/qms-service/common/getTestTools", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getTestTools(@RequestBody Map<String, Object> condMap);

	@RequestMapping(value = "/qms-service/common/getQmsTestRecords", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R getQmsTestRecords(@RequestBody Map<String, Object> condMap);

}

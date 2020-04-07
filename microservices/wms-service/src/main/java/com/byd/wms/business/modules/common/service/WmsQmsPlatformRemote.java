package com.byd.wms.business.modules.common.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "WMS-WEBSERVICE")
public interface WmsQmsPlatformRemote {

	@RequestMapping(value = "/wms-webservice/qmswebservice/sendQmsData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R sendQmsData(@RequestBody Map<String, Object> paramMap);
}

package com.byd.web.wms.query.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsWhTaskQueryRemote {

	@RequestMapping(value = "/wms-service/query/taskQuery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params);
}

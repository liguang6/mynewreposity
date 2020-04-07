package com.byd.web.wms.report.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;



@FeignClient(name = "WMS-SERVICE")
public interface EngineMaterialRemote {

    @RequestMapping(value = "/wms-service/report/engineMaterial/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params);

    @RequestMapping(value = "/wms-service/report/engineMaterial/queryProject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> queryProject(@RequestParam Map<String, Object> map);
 
}

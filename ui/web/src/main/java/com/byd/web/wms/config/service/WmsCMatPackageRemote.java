package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCMatPackageRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/matPackage/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/matPackage/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/matPackage/merge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R merge(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/config/matPackage/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestParam(value = "params") Map<String, Object> params);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/matPackage/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestParam(value = "params") Map<String,Object> params);
   
}

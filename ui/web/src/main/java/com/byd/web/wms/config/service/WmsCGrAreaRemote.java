package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCGrAreaEntity;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCGrAreaRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/area/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/area/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value = "id") Long id);
    
	@RequestMapping(value = "/wms-service/config/area/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCGrAreaEntity area);

	@RequestMapping(value = "/wms-service/config/area/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCGrAreaEntity area);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/area/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCGrAreaEntity area);
    
}

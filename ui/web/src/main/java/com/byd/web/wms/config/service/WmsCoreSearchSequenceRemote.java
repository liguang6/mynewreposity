package com.byd.web.wms.config.service;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCoreSearchSequenceEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCoreSearchSequenceRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/wmsCoreSearchSequence/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/wmsCoreSearchSequence/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value = "id") Long id);
    
	@RequestMapping(value = "/wms-service/config/wmsCoreSearchSequence/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCoreSearchSequenceEntity entity);

	@RequestMapping(value = "/wms-service/config/wmsCoreSearchSequence/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCoreSearchSequenceEntity entity);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/wmsCoreSearchSequence/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCoreSearchSequenceEntity entity);
    
	@RequestMapping(value = "/wms-service/config/wmsCoreSearchSequence/queryAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryAll(@RequestParam Map<String, Object> params);
}

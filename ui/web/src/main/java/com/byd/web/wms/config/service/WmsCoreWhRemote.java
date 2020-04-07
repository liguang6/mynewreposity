package com.byd.web.wms.config.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCoreWhEntity;
import com.byd.web.wms.config.entity.WmsCoreWh_AddressEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCoreWhRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/wh/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/wh/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/wh/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCoreWh_AddressEntity wh);

	@RequestMapping(value = "/wms-service/config/wh/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCoreWh_AddressEntity wh);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/wh/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestParam(value = "params") Map<String, Object> params);
    
}

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
import org.springframework.web.multipart.MultipartFile;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsSapPlantLgortEntity;


@FeignClient(name = "WMS-SERVICE")
public interface WmsSapPlantLgortRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/sapPlantLgort/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/sapPlantLgort/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody WmsSapPlantLgortEntity entity);
	
	@RequestMapping(value = "/wms-service/config/sapPlantLgort/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody WmsSapPlantLgortEntity entity);
	
	@RequestMapping(value = "/wms-service/config/sapPlantLgort/get", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getOne(@RequestParam(value="id")Long id);
	
	@RequestMapping(value = "/wms-service/config/sapPlantLgort/delFlag", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delFlag(@RequestParam(value="id") Long id);
	
	@RequestMapping(value = "/wms-service/config/sapPlantLgort/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R preview(@RequestBody List<WmsSapPlantLgortEntity> listEntity);
	
	@RequestMapping(value = "/wms-service/config/sapPlantLgort/importWmsSapPlantLgortEntity", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R importWmsSapPlantLgortEntity(@RequestBody List<WmsSapPlantLgortEntity> listEntity);
}

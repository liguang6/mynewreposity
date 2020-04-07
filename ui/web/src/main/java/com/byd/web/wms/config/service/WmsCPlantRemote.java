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
import com.byd.web.wms.config.entity.WmsCMatDangerEntity;
import com.byd.web.wms.config.entity.WmsCPlant;
import com.byd.web.wms.config.entity.WmsCWhEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCPlantRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/CWh/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/CWh/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value = "id") Long id);
    
	@RequestMapping(value = "/wms-service/config/CWh/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCWhEntity entity);

	@RequestMapping(value = "/wms-service/config/CWh/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCWhEntity entity);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/CWh/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCPlant entity);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/CWh/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<WmsCWhEntity> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/CWh/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<WmsCWhEntity> entityList);
	
	@RequestMapping(value = "/wms-service/config/CWh/queryPlantName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPlantName(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/CWh/queryEntity", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryEntity(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/CWh/deletes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deletes(@RequestParam(value = "ids") String ids);
}

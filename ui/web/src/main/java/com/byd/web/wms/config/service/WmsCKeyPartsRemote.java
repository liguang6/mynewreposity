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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCKeyPartsEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCKeyPartsRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/keyparts/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/keyparts/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/keyparts/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCKeyPartsEntity wmsCKeyParts);

	@RequestMapping(value = "/wms-service/config/keyparts/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCKeyPartsEntity wmsCKeyParts);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/keyparts/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCKeyPartsEntity wmsCKeyParts);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/keyparts/batchSave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R batchSave(@RequestBody Map<String,Object> params) ;
	/**
	 * 粘贴数据校验
	 */
	@RequestMapping(value = "/wms-service/config/keyparts/checkPasteData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R checkPasteData(@RequestBody Map<String,Object> params);
    
}

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
import com.byd.web.wms.config.entity.WmsCoreWhBinEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCoreWhBinRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/corewhbin/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/corewhbin/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value = "id") Long id);
    
	@RequestMapping(value = "/wms-service/config/corewhbin/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCoreWhBinEntity wmsCoreWhBin);

	@RequestMapping(value = "/wms-service/config/corewhbin/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCoreWhBinEntity wmsCoreWhBin);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/corewhbin/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCoreWhBinEntity wmsCoreWhBin);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/corewhbin/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<WmsCoreWhBinEntity> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/corewhbin/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R preview(@RequestBody List<Map<String,Object>> params);
	/*
	 * 根据仓库号和储位查询
	 */
	@RequestMapping(value = "/wms-service/config/corewhbin/queryBinCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryBinCode(@RequestParam(value = "params") Map<String, Object> params);
    
}

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
import com.byd.web.wms.config.entity.WmsCQcPlantEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCQcPlantRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/wmscqcplant/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/wmscqcplant/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/wmscqcplant/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCQcPlantEntity wmsCQcMat);

	@RequestMapping(value = "/wms-service/config/wmscqcplant/importBatch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R importBatch(@RequestBody List<WmsCQcPlantEntity> entitys);
	
	@RequestMapping(value = "/wms-service/config/wmscqcplant/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCQcPlantEntity wmsCQcMat);
	
	@RequestMapping(value = "/wms-service/config/wmscqcplant/saveOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveOrUpdate(@RequestBody WmsCQcPlantEntity wmsCQcMat);
	
	@RequestMapping(value = "/wms-service/config/wmscqcplant/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestBody Long[] ids);
    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/wmscqcplant/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestParam(value="id") Long id);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/wmscqcplant/imports", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R imports(@RequestBody List<WmsCQcPlantEntity> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/wmscqcplant/previewExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<WmsCQcPlantEntity> entityList);
    
}

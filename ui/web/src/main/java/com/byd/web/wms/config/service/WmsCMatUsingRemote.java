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
import com.byd.web.wms.config.entity.WmsCMatUsingEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCMatUsingRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/matusing/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/matusing/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/matusing/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCMatUsingEntity wmsCMatUsing);

	@RequestMapping(value = "/wms-service/config/matusing/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCMatUsingEntity wmsCMatUsing);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/matusing/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCMatUsingEntity wmsCMatUsing);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/matusing/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<Map<String,Object>> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/matusing/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<Map<String,Object>> entityList);
    
}

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
import com.byd.web.wms.config.entity.WmsCQcMatEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCQcMatRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/wmscqcmat/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/wmscqcmat/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/wmscqcmat/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCQcMatEntity wmsCQcMat);

	@RequestMapping(value = "/wms-service/config/wmscqcmat/importBatch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R importBatch(@RequestBody List<WmsCQcMatEntity> entitys);
	
	@RequestMapping(value = "/wms-service/config/wmscqcmat/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCQcMatEntity wmsCQcMat);
	
	@RequestMapping(value = "/wms-service/config/wmscqcmat/save_update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveOrUpdate(@RequestBody WmsCQcMatEntity wmsCQcMat);
	
	@RequestMapping(value = "/wms-service/config/wmscqcmat/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestBody Long[] ids);
    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/wmscqcmat/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestParam(value="id") Long id);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/wmscqcmat/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<Map<String,Object>> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/wmscqcmat/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<Map<String, Object>> params);
    
}

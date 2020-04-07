package com.byd.web.wms.config.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCQcMatSampleEntity;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCQcMatSampleRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCQcMatSampleEntity wmsCQcMatSample);

	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/importBatch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R importBatch(@RequestBody List<WmsCQcMatSampleEntity> entitys);
	
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCQcMatSampleEntity wmsCQcMatSample);
	
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/saveOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveOrUpdate(@RequestBody WmsCQcMatSampleEntity wmsCQcMatSample);
	
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestBody Long[] ids);
    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestParam(value="id") Long id);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<Map<String,Object>> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/wmscqcmatsample/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R preview(@RequestBody List<Map<String,Object>> entityList);
    
}

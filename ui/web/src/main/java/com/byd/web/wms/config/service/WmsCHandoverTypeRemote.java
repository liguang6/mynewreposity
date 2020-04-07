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
import com.byd.web.wms.config.entity.WmsCMatUsingEntity;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCHandoverTypeRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/handoverType/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/handoverType/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/handoverType/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> wmsCMatUsing);

	@RequestMapping(value = "/wms-service/config/handoverType/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody Map<String, Object> wmsCMatUsing);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/handoverType/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody Map<String,Object> wmsCMatUsing);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/handoverType/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<Map<String,Object>> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/handoverType/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<Map<String,Object>> entityList);
	
	@RequestMapping(value = "/wms-service/config/handoverType/wmsCHandoverTypelist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R wmsCHandoverTypelist(@RequestParam(value = "params") Map<String, Object> params);
	/**
     * 复制保存
     * @param entitys
     * @return
     */
	@RequestMapping(value = "/wms-service/config/handoverType/saveCopyData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveCopyData(@RequestBody List<Map<String, Object>> list);
}

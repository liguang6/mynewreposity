package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCMatManagerTypeRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/matmanagertype/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/matmanagertype/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/matmanagertype/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/config/matmanagertype/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody Map<String, Object> params);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/matmanagertype/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody Map<String, Object> params);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/matmanagertype/batchSave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R batchSave(@RequestBody Map<String,Object> params) ;
	/**
	 * 粘贴数据校验
	 */
	@RequestMapping(value = "/wms-service/config/matmanagertype/checkPasteData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R checkPasteData(@RequestBody Map<String,Object> params);

	@RequestMapping(value = "/wms-service/config/matmanagertype/getLgortSelect", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getLgortSelect(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/matmanagertype/getWhAreaSelect", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWhAreaSelect(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/matmanagertype/getAuthorityCodeSelect", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getAuthorityCodeSelect(@RequestBody Map<String, Object> params);
}

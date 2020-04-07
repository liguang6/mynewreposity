package com.byd.web.wms.config.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCVendor;


@FeignClient(name = "WMS-SERVICE")
public interface WmsCVendorRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/CVendor/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/CVendor/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R add(@RequestBody WmsCVendor entity);
	/**
     * 根据ID查询实体记录
     */
	@RequestMapping(value = "/wms-service/config/CVendor/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value="id") Long id);

	@RequestMapping(value = "/wms-service/config/CVendor/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody WmsCVendor entity);

	@RequestMapping(value = "/wms-service/config/CVendor/del", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestParam(value="id")Long id);

	@RequestMapping(value = "/wms-service/config/CVendor/dels", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deletes(@RequestParam(value="ids")String ids);
    
	@RequestMapping(value = "/wms-service/config/CVendor/listEntity", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryEntity(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/CVendor/listPlantName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPlantName(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/CVendor/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<WmsCVendor> entityList);
	
	@RequestMapping(value = "/wms-service/config/CVendor/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<WmsCVendor> entityList);
	
	@RequestMapping(value = "/wms-service/config/CVendor/listVendortName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryVendorName(@RequestParam Map<String, Object> params);

	@RequestMapping(value = "/wms-service/config/CVendor/querySAPVendorName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R querySAPVendorName(@RequestParam Map<String, Object> params);
}

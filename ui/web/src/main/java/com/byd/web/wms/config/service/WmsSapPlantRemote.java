package com.byd.web.wms.config.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsSapPlant;


@FeignClient(name = "WMS-SERVICE")
public interface WmsSapPlantRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/sapPlant/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/sapPlant/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestParam(value = "id") Long id);
	
	@RequestMapping(value = "/wms-service/config/sapPlant/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R add(@RequestBody WmsSapPlant entity);
	
	@RequestMapping(value = "/wms-service/config/sapPlant/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody WmsSapPlant entity);
	
	@RequestMapping(value = "/wms-service/config/sapPlant/get", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWmsSapPlantById(@RequestParam(value="id") Long id);
	@RequestMapping(value = "/wms-service/config/sapPlant/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R uploadWmsSapPlant(@RequestBody List<WmsSapPlant> entityList) ;
	
	@RequestMapping(value = "/wms-service/config/sapPlant/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<WmsSapPlant> entityList);
	
	/**
	 * 导出excel文件.
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/wms-service/config/sapPlant/exportExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> exportExcel(@RequestBody WmsSapPlant entity);
	
	@RequestMapping(value = "/wms-service/config/sapPlant/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPlants(@RequestParam(value="params") Map<String,Object> params);
}

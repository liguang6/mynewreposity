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

/**
 * @author ren.wei3
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCMatFixedStorageRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/fixedStorage/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/fixedStorage/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value = "id") Long id);
	
	@RequestMapping(value = "/wms-service/config/fixedStorage/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/fixedStorage/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/fixedStorage/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delById(@RequestParam(value = "ids") String ids);
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/fixedStorage/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<Map<String,Object>> params);
	
	/**
	 * 批量保存
	 */
	@RequestMapping(value = "/wms-service/config/fixedStorage/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<Map<String,Object>> entityList) ;
}

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
import com.byd.web.wms.config.entity.WmsCMatStorageEntity;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCMatStorageRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/matstorage/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/matstorage/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/matstorage/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCMatStorageEntity wmsCMatStorage);

	@RequestMapping(value = "/wms-service/config/matstorage/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCMatStorageEntity wmsCMatStorage);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/matstorage/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCMatStorageEntity wmsCMatStorage);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/matstorage/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<Map<String,Object>> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/matstorage/previewExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<Map<String,Object>> params);
	
	@RequestMapping(value = "/wms-service/config/matstorage/queryBinCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryBinCode(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/matstorage/queryAreaCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryAreaCode(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/matstorage/queryCtrFlag", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryCtrFlag(@RequestParam Map<String, Object> params);
    
}

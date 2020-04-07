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
import org.springframework.web.multipart.MultipartFile;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCQcResultEntity;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCQcResultRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/wmscqcresult/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/wmscqcresult/list2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list2(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/wmscqcresult/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/wmscqcresult/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCQcResultEntity entity);

	@RequestMapping(value = "/wms-service/config/wmscqcresult/importBatch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R importBatch(@RequestBody List<WmsCQcResultEntity> entitys);
	
	@RequestMapping(value = "/wms-service/config/wmscqcresult/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCQcResultEntity entity);
	
	@RequestMapping(value = "/wms-service/config/wmscqcresult/saveOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveOrUpdate(@RequestBody WmsCQcResultEntity entity);
	
	@RequestMapping(value = "/wms-service/config/wmscqcresult/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestBody Long[] ids);
    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/wmscqcresult/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCQcResultEntity entity);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/wmscqcresult/importExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R importExcel(@RequestBody List<WmsCQcResultEntity> entityList) ;
	/**
	 * 导入预览
	 */
	@RequestMapping(value = "/wms-service/config/wmscqcresult/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<WmsCQcResultEntity> entityList);
	
	@RequestMapping(value = "/wms-service/config/wmscqcresult/batchSave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R batchSave(@RequestBody Map<String, Object> params);
    
}

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
import com.byd.web.wms.config.entity.WmsInPoItemAuthEntity;

@FeignClient(name = "WMS-SERVICE")
public interface WmsInPoItemAuthRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/poAuth/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/poAuth/getPolist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getPolist(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/config/poAuth/PoAuthSave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R savePoData(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 根据ID查询实体记录
     */
	@RequestMapping(value = "/wms-service/config/poAuth/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
	@RequestMapping(value = "/wms-service/config/poAuth/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody WmsInPoItemAuthEntity entity);
    
	@RequestMapping(value = "/wms-service/config/poAuth/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestParam(value="id")Long id);
    
	@RequestMapping(value = "/wms-service/config/poAuth/dels", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deletes(@RequestParam(value = "ids") String ids);
	
	@RequestMapping(value = "/wms-service/config/poAuth/queryByEbeln", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryByEbeln(@RequestParam(value="params") Map<String, Object> params);
    /**
     * 小写的参数
     * @param params
     * @return
     */
	@RequestMapping(value = "/wms-service/config/poAuth/queryByEbelnMin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryByEbelnMin(@RequestParam(value="params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/poAuth/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<WmsInPoItemAuthEntity> entityList);

	@RequestMapping(value = "/wms-service/config/poAuth/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestParam(value="params") Map<String, Object> params);
}

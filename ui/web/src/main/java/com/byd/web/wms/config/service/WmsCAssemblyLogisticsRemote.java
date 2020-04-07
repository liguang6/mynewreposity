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
import com.byd.web.wms.config.entity.WmsCAssemblyLogisticsEntity;
import com.byd.web.wms.config.entity.WmsCKeyPartsEntity;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * 
 * @version 创建时间：2019年7月26日 下午3:34:29 
 * 类说明 
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCAssemblyLogisticsRemote {
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/assemblylogistics/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/assemblylogistics/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics);
	
	/**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/assemblylogistics/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/wms-service/config/assemblylogistics/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics);

	@RequestMapping(value = "/wms-service/config/assemblylogistics/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics);
	
	/**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/assemblylogistics/batchSave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R batchSave(@RequestBody Map<String,Object> params) ;
	/**
	 * 粘贴数据校验
	 */
	@RequestMapping(value = "/wms-service/config/assemblylogistics/checkPasteData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R checkPasteData(@RequestBody Map<String,Object> params);

}

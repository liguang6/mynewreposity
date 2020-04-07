package com.byd.web.wms.config.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCPlantToRemote {

	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/cPlantTo/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
	/**
	 * 保存
	 * @param params
	 */
	@RequestMapping(value = "/wms-service/config/cPlantTo/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public void save(@RequestBody Map<String, Object> params);
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/wms-service/config/cPlantTo/deletes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deletes(@RequestParam(value = "ids") String ids);
	
	
	
}

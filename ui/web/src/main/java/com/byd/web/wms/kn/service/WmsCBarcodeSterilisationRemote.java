package com.byd.web.wms.kn.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCBarcodeSterilisationRemote {
	/**
	 *
	 * 列表
	 */
	@RequestMapping(value = "/wms-service/kn/barcodeSterilisation/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;

	@RequestMapping(value = "/wms-service/kn/barcodeSterilisation/one", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R one(@RequestParam Map<String, Object> params) ;
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/wms-service/kn/barcodeSterilisation/saveCoreLabel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveCoreLabel(@RequestParam Map<String, Object> params) ;
	
	/**
	 * 
	 * 打印标签
	 */
	@RequestMapping(value = "/wms-service/kn/barcodeSterilisation/labelLabelPreview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R printCoreLabel(@RequestParam Map<String, Object> params) ;
	

}

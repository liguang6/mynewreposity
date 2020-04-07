package com.byd.web.wms.kn.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCBarcodeStorageRemote {
	/**
	 *
	 * 列表
	 */
	@RequestMapping(value = "/wms-service/kn/barcodeStorage/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;

	@RequestMapping(value = "/wms-service/kn/barcodeStorage/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R importExel(@RequestBody List<Map<String, Object>> params) ;
	

	

}

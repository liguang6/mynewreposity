package com.byd.web.kn.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/*411K
 * @author chen.yafei
 * @email chen.yafei1@byd.com
 * @date 2019-12-27
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsKnStockTransferCPdaRemote {

	@RequestMapping(value = "/wms-service/knpda/stockTransferC/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R list(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/knpda/stockTransferC/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R save(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/knpda/stockTransferC/labelList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R labelList(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/knpda/stockTransferC/deleteLabel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R deleteLabel(@RequestBody Map<String, Object> params);

}

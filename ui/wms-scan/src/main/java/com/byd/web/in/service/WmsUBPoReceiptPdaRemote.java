package com.byd.web.in.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author rain
 * @version 2019年12月26日10:13:22
 * PDA-UB订单101收货
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsUBPoReceiptPdaRemote {

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/getMorecCache", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getMorecCache(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/validatePoReceiptLable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R validatePoReceiptLable(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/getMoDetailByBarcode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getMoDetailByBarcode(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/getGridPoreData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getGridPoreData(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/getBarGridPoreData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getBarGridPoreData(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/getGridReqItemData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getGridReqItemData(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/poReDeleteBarInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R poReDeleteBarInfo(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/ubPOReceiptPdaIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R ubPOReceiptPdaIn(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsubporeceiptpda/createheadText", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R createheadText(@RequestBody Map<String, Object> params);

}

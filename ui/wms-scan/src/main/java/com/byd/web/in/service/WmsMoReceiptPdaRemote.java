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
 * @version 2019年12月22日11:22:24
 * PDA生产订单101收货
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsMoReceiptPdaRemote {

	@RequestMapping(value = "/wms-service/inpda/wmsmoreceiptpda/getMorecCache", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getMorecCache(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsmoreceiptpda/validatePoReceiptLable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R validatePoReceiptLable(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsmoreceiptpda/getMoDetailByBarcode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getMoDetailByBarcode(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsmoreceiptpda/getGridPoreData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getGridPoreData(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsmoreceiptpda/getBarGridPoreData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getBarGridPoreData(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsmoreceiptpda/poReDeleteBarInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R poReDeleteBarInfo(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsmoreceiptpda/moReceiptPdaIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R moReceiptPdaIn(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsmoreceiptpda/createheadText", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R createheadText(@RequestBody Map<String, Object> params);

}

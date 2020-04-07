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
 * @version 2019年12月4日20:28:52
 * PDA采购订单收货
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsPoReceiptPdaRemote {

	@RequestMapping(value = "/wms-service/inpda/wmsporeceiptpda/getPorecCache", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getPorecCache(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsporeceiptpda/validatePoReceiptLable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R validatePoReceiptLable(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsporeceiptpda/getPoDetailByBarcode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getPoDetailByBarcode(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsporeceiptpda/getGridPoreData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getGridPoreData(@RequestParam(value="params")  Map<String,Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsporeceiptpda/getBarGridPoreData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getBarGridPoreData(@RequestParam(value="params")  Map<String,Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsporeceiptpda/poReDeleteBarInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R poReDeleteBarInfo(@RequestParam(value="params")  Map<String,Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsporeceiptpda/poReceiptPdaIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R poReceiptPdaIn(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/inpda/wmsporeceiptpda/createheadText", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R createheadText(@RequestBody Map<String, Object> params);

}

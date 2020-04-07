package com.byd.web.returngoods.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author liguang6
 * @version 2019年1月2日12:28:34
 * 立库余料退回
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsReMaterialWarehouseRemote {

	@RequestMapping(value = "/wms-service/retrunpda/wmsReMaterialWarehouse/getPorecCache", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getPorecCache(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/retrunpda/wmsReMaterialWarehouse/validatePoReceiptLable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R validatePoReceiptLable(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/retrunpda/wmsReMaterialWarehouse/getPoDetailByBarcode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getPoDetailByBarcode(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/retrunpda/wmsReMaterialWarehouse/getGridPoreData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getGridPoreData(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/retrunpda/wmsReMaterialWarehouse/getBarGridPoreData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getBarGridPoreData(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/retrunpda/wmsReMaterialWarehouse/poReDeleteBarInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R poReDeleteBarInfo(@RequestParam(value = "params") Map<String, Object> params);

	@RequestMapping(value = "/wms-service/retrunpda/wmsReMaterialWarehouse/poReceiptPdaIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R poReceiptPdaIn(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/wms-service/retrunpda/wmsReMaterialWarehouse/createheadText", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R createheadText(@RequestBody Map<String, Object> params);

}

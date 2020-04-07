package com.byd.web.wms.returngoods.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface ReturnGoodsRemote {
	
	@RequestMapping(value = "/wms-service/returngoods/getBusinessNameListByWerks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getBusinessNameListByWerks(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getReceiveRoomOutData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getReceiveRoomOutData(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutPickupData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutPickupData(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/createReceiveRoomOutReturn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R createReceiveRoomOutReturn(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutReturnData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutReturnData(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getReceiveRoomOutReturnData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getReceiveRoomOutReturnData(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/confirmReceiveRoomOutReturn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R confirmReceiveRoomOutReturn(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/confirmWareHouseOutReturn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R confirmWareHouseOutReturn(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutData37", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutData37(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutData33", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutData33(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutData34", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutData34(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutData30", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutData30(@RequestParam(value = "paramMap") Map<String,Object> paramMap);

	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutData31", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutData31(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutData29", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutData29(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutData28", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutData28(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/getWareHouseOutData27", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWareHouseOutData27(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/createWareHouseOutReturn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R createWareHouseOutReturn(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/wareHouseOutPickup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R wareHouseOutPickup(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
	@RequestMapping(value = "/wms-service/returngoods/wareHouseOutPickupCancel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R wareHouseOutPickupCancel(@RequestParam(value = "paramMap") Map<String,Object> paramMap);
	
}

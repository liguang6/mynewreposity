package com.byd.web.wms.returngoods.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WorkshopReturnRemote {
	@RequestMapping(value = "/wms-service/workshopReturn/getBusinessNameListByWerks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getBusinessNameListByWerks(@RequestParam Map<String, Object> paramMap);
    
	@RequestMapping(value = "/wms-service/workshopReturn/getSapInOrderInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getSapInOrderInfo(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/wms-service/workshopReturn/getSapVendorInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getSapVendorInfo(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/wms-service/workshopReturn/getCostCenterInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getCostCenterInfo(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/wms-service/workshopReturn/getWbsInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWbsInfo(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/wms-service/workshopReturn/getWorkshopReturnData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWorkshopReturnData(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/wms-service/workshopReturn/getWorkshopReturnConfirmData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWorkshopReturnConfirmData(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/wms-service/workshopReturn/getSapMoComponentDate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getSapMoComponentDate(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/wms-service/workshopReturn/createWorkshopReturn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R createWorkshopReturn(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/wms-service/workshopReturn/confirmWorkshopReturn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R confirmWorkshopReturn(@RequestParam Map<String, Object> paramMap);
}

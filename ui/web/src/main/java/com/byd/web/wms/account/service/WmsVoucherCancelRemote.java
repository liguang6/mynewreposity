package com.byd.web.wms.account.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsVoucherCancelRemote {
	
	@RequestMapping(value = "/wms-service/account/wmsVoucherCancel/getVoucherCancelData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getVoucherCancelData(@RequestParam Map<String, Object> params) ;
	
	@RequestMapping(value = "/wms-service/account/wmsVoucherCancel/confirmVoucherCancelData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R confirmVoucherCancelData(@RequestParam Map<String, Object> params) ;
	
}

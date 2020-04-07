package com.byd.web.zzjmes.product.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "ZZJMES-SERVICE")
public interface ProductionExceptionRemote {
	@RequestMapping(value = "/zzjmes-service/pmdManager/getProductionExceptionPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getProductionExceptionPage(@RequestParam Map<String, Object> paramMap);
}

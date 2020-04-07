package com.byd.web.bjmes.product.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.byd.utils.R;

@FeignClient(name = "BJMES-SERVICE")
public interface PlanProductExecuteRemote {

	@RequestMapping(value = "/bjmes-service/product/pe/getOrderProducts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getOrderProducts(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/bjmes-service/product/pe/getProductPlanList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getProductPlanList(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/bjmes-service/product/pe/saveProductPlan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R saveProductPlan(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/bjmes-service/product/pe/getScanProductInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getScanProductInfo(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/bjmes-service/product/pe/getPDProductList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getPDProductList(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/bjmes-service/product/pe/saveProductScan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R saveProductScan(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/bjmes-service/product/pe/getProductByKeyparts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getProductByKeyparts(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/bjmes-service/product/pe/getProductPDList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getProductPDList(@RequestBody Map<String, Object> params);

}

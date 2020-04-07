package com.byd.web.bjmes.product.service;

import java.util.Map;
import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;

@FeignClient(name = "BJMES-SERVICE")
public interface ProductManageRemote {
  @RequestMapping(value = "/bjmes-service/productManage/getProductsForNoManage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public R getProductsForNoManage(@RequestParam Map<String, Object> paramMap);
  
  @RequestMapping(value = "/bjmes-service/productManage/generateProductsNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public R generateProductsNo(@RequestParam Map<String, Object> paramMap);
  
  @RequestMapping(value = "/bjmes-service/productManage/getOrderList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public R getOrderList(@RequestParam Map<String, Object> paramMap);
  
  @RequestMapping(value = "/bjmes-service/productManage/getExceptionList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public R getExceptionList(@RequestParam Map<String, Object> paramMap);
  
  @RequestMapping(value = "/bjmes-service/productManage/getProductNoinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public R getProductNoinfo(@RequestParam Map<String, Object> paramMap);

  @RequestMapping(value = "/bjmes-service/productManage/insertProductionException", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public R insertProductionException(@RequestParam Map<String, Object> paramMap);

  @RequestMapping(value = "/bjmes-service/productManage/editProductionException", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public R editProductionException(@RequestParam Map<String, Object> paramMap);
}
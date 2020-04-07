package com.byd.web.bjmes.product.controller;

import java.util.Map;
import com.byd.utils.R;
import com.byd.web.bjmes.product.service.ProductManageRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bjmes/productManage")
public class ProductManageController {
  @Autowired
  private ProductManageRemote productManageRemote;

  @RequestMapping("/getProductsForNoManage")
  public R getProductsForNoManage(@RequestParam Map<String, Object> params){
    return productManageRemote.getProductsForNoManage(params);
  }

  @RequestMapping("/generateProductsNo")
  public R generateProductsNo(@RequestParam Map<String, Object> params){
    return productManageRemote.generateProductsNo(params);
  }
  
  @RequestMapping("/getOrderList")
  public R getOrderList(@RequestParam Map<String, Object> params){
    return productManageRemote.getOrderList(params);
  }

  @RequestMapping("/getExceptionList")
  public R getExceptionList(@RequestParam Map<String, Object> params){
    return productManageRemote.getExceptionList(params);
  }

  @RequestMapping("/getProductNoinfo")
  public R getProductNoinfo(@RequestParam Map<String, Object> params){
    return productManageRemote.getProductNoinfo(params);
  }

  @RequestMapping("/insertProductionException")
  public R insertProductionException(@RequestParam Map<String, Object> params){
    return productManageRemote.insertProductionException(params);
  }

  @RequestMapping("/editProductionException")
  public R editProductionException(@RequestParam Map<String, Object> params){
    return productManageRemote.editProductionException(params);
  }
}
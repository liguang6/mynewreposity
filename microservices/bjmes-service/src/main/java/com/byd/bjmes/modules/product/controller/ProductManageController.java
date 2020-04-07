package com.byd.bjmes.modules.product.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.byd.bjmes.modules.product.service.ProductManageService;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("productManage")
public class ProductManageController {
  @Autowired
  private UserUtils userUtils;
  @Autowired
  private ProductManageService productManageService;

  @RequestMapping("/getProductsForNoManage")
  public R getProductsForNoManage(@RequestParam Map<String,Object> params){
    List<Map<String,Object>> list = productManageService.getProductsForNoManage(params);
    return R.ok().put("data", list);
  }

  @RequestMapping("/generateProductsNo")
  public R generateProductsNo(@RequestParam Map<String,Object> params){
    int result = productManageService.generateProductsNo(params);
    return R.ok().put("result", result);
  }

  @RequestMapping("/getOrderList")
  public R getOrderList(@RequestParam Map<String,Object> params){
    return R.ok().put("page", productManageService.getOrderList(params));
  }

  @RequestMapping("/getExceptionList")
  public R getExceptionList(@RequestParam Map<String,Object> params){
    List<Map<String,Object>> list = productManageService.getExceptionList(params);
    return R.ok().put("result", list);
  }

  @RequestMapping("/getProductNoinfo")
  public R getProductNoinfo(@RequestParam Map<String,Object> params){
    List<Map<String,Object>> list = productManageService.getProductNoinfo(params);
    return R.ok().put("result", list);
  }

  @RequestMapping("/insertProductionException")
  public R insertProductionException(@RequestParam Map<String,Object> params){
    Map<String,Object> currentUser = userUtils.getUser();
    params.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
		params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    int result = productManageService.insertProductionException(params);
    return R.ok().put("result", result);
  }

  @RequestMapping("/editProductionException")
  public R editProductionException(@RequestParam Map<String,Object> params){
    Map<String,Object> currentUser = userUtils.getUser();
    params.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
		params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    int result = productManageService.editProductionException(params);
    return R.ok().put("result", result);
  }
  
}
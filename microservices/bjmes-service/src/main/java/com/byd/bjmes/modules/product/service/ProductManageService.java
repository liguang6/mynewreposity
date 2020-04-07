package com.byd.bjmes.modules.product.service;

import java.util.List;
import java.util.Map;
import com.byd.utils.PageUtils;

public interface ProductManageService {
  public List<Map<String,Object>> getProductsForNoManage(Map<String, Object> condMap);

  public int generateProductsNo(Map<String, Object> condMap);

  public PageUtils getOrderList(Map<String, Object> params);

  public List<Map<String, Object>> getExceptionList(Map<String, Object> params);
  public List<Map<String, Object>> getProductNoinfo(Map<String, Object> params);

  public int insertProductionException(Map<String, Object> params);
  public int editProductionException(Map<String, Object> params);
}
package com.byd.bjmes.modules.product.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface ProductManageDao {
  public List<Map<String,Object>> getProductsForNoManage(Map<String, Object> params);

  public List<Map<String,Object>> getProductsForGenerateNo(Map<String, Object> params);
  public int getProductsNoCount(Map<String, Object> params);
  public int getProductsNoMax(Map<String, Object> params);

  public int insertProductsNo(@Param("matList")List<Map<String,Object>> matList);

  public List<Map<String, Object>> getOrderList(Map<String, Object> params);
  public int getOrderListTotalCount(Map<String, Object> params);
  
  public List<Map<String, Object>> getExceptionList(Map<String, Object> params);
  public List<Map<String, Object>> getProductNoinfo(Map<String, Object> params);
  public int insertProductionException(Map<String, Object> params);
  public int editProductionException(Map<String, Object> params);
}
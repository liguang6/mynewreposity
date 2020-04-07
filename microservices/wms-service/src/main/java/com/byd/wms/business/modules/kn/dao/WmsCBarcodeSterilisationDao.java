package com.byd.wms.business.modules.kn.dao;

import java.util.List;
import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
public interface WmsCBarcodeSterilisationDao  {

     List<Map<String, Object>> queryBarcodeSterilisation(Map<String, Object> params) ;
      int getListCount(Map<String, Object> params) ;

    Map<String, Object> queryBarcodeSterilisationOne(Map<String, Object> params);

    List<Map<String, Object>> queryBarcode(List labels);

    int getBarcodeCount(List labels);
}

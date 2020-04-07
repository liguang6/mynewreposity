package com.byd.wms.webservice.ws.dao;

import java.util.List;
import java.util.Map;

public interface WmsStockQueryDao {
    //调用库存查询接口
    List<Map<String,Object>> getQueryStock(Map<String,Object> map);
    List<Map<String,Object>> getWERKS(Map<String,Object> map);

}

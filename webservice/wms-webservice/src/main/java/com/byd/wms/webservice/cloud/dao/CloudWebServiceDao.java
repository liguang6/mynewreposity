package com.byd.wms.webservice.cloud.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date $time$ $date$
 * @Description //TODO $end$
 **/

public interface CloudWebServiceDao {

    public boolean insertWmsCloudDelivery(HashMap hm);
    public boolean insertWmsCloudDeliveryItem(List<Map<String, Object>> hm);
    public boolean insertWmsCloudPacking(HashMap hm);
    public boolean updateWmsCloudPacking(HashMap hm);


    public List<Map<String,Object>> queryWhDetail(HashMap hm);




}

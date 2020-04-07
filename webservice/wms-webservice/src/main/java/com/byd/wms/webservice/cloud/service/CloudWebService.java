package com.byd.wms.webservice.cloud.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date $time$ $date$
 * @Description //TODO $end$
 **/

public interface CloudWebService {
    /**
     * 读取送货单
     */
    public HashMap deliveryWmsService(HashMap hashMap);

    /**
     *收货后返回条码信息
     */
    public HashMap sendDeliveryData(HashMap hashMap);
    
    public HashMap getDeliveryDataByBarcode(HashMap params);
    public HashMap getDeliveryData(HashMap params);
}

package com.byd.wms.business.modules.in.dao;

import java.util.List;
import java.util.Map;

public interface WmsSTOReceiptPdaDao {
    /**
     *获取发货单包装箱信息或者标签信息
     * @param params
     */
    List<Map<String, Object>> getDeliveryPacking(Map<String, Object> params);

    int getSTOCacheCount(Map<String, Object> params);

    List<Map<String, Object>> defaultSTOCache(Map<String, Object> params);

    List<Map<String, Object>> validateStorage(Map<String, Object> params);

    void calcOpsTime(Map<String, Object> params);

    Map<String, Object> getfirstOpsTime(Map<String, Object> params);

}

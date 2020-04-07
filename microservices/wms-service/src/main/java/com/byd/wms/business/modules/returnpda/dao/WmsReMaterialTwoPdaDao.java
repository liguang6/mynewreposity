package com.byd.wms.business.modules.returnpda.dao;

import java.util.List;
import java.util.Map;

/**
 * @author liguang6
 * @date 2019/12/12 13:12
 * @title
 */
public interface WmsReMaterialTwoPdaDao {
    Map<String, Object> validateScanCache(Map<String, Object> params);//PDA扫描缓存表【WMS_PDA_SCAN_CACHE】是否有对应账号和业务类型的数据
    List<Map<String, Object>> getPorecCache(Map<String, Object> params);//获取描缓存表对应数据
    List<Map<String, Object>> getScanInfo(Map<String, Object> params);//获取条码表明细
    Map<String, Object> getWmsDocByLabelNo(Map<String, Object> params);
    Map<String, Object> getWmsQTYByLabel(Map<String, Object> params);
    Map<String, Object> getWmsBatchByLabel(Map<String, Object> params);
    void insertScanCache(Map<String, Object> params);
    void  insertBarCodeLog(Map<String, Object> params);
    void insertWmsPZ(Map<String, Object> params);
    void updataCoreLabel(Map<String, Object> params);
    int removeScanInfo(Map<String,Object> params);
    void saveData(Map<String,Object> params);
}

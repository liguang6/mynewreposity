package com.byd.wms.business.modules.returnpda.service;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.List;
import java.util.Map;

/**
 * @author liguang6
 * @date 2019/12/12 11:02
 * @title
 */

public interface WmsReMaterialPdaService {
    public PageUtils getPorecCache(Map<String,Object> params);
    public PageUtils getScanInfo(Map<String,Object> params);
    public Map<String, Object> getWmsDocByLabelNo(Map<String,Object> params);
    public Map<String, Object> confirmWorkshopReturn(Map<String,Object> params);
    public R validateScanCache(Map<String,Object> params);
    public void insertScanCache(Map<String,Object> params);
    public void  insertBarCodeLog(Map<String,Object> params);
    public void insertWmsPZ(Map<String,Object> params);
    public void updataCoreLabel(Map<String,Object> params);
    public int removeScanInfo(Map<String,Object> params);
    public Map<String, Object> confirmWorkshopReturned(Map<String,Object> params);
    public  void saveData(Map<String,Object> params);
    public List<Map<String, Object>> getAllScanInfo(Map<String,Object> params);
}

package com.byd.wms.business.modules.out.service;

import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;

import java.util.List;
import java.util.Map;

/**
 * WMS出库需求拣配
 * @author ren.wei3
 * @date 2019-04-11
 */

public interface WmsOutPickingService {

	public PageUtils queryPage(Map<String, Object> params);
    
    public PageUtils recommend(Map<String, Object> params);
    
    public void picking(Map<String, Object> params);
    
    public PageUtils queryShippingLabel(Map<String, Object> params);
    
    public List<Map<String, Object>> saveShippingLabel(Map<String, Object> params);
    
    public PageUtils selectPickList(Map<String, Object> params);
    
    public List<WmsOutRequirementHeadEntity> getReq(Map<String, Object> params);
    
    public PageUtils queryShippingLabelcs(Map<String, Object> params);
    
    public PageUtils queryKeyPartsLabel(Map<String, Object> params);
    
}


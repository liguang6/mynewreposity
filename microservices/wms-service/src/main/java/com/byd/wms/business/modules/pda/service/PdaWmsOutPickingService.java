package com.byd.wms.business.modules.pda.service;

import java.util.List;
import java.util.Map;

/**
 * pda出库需求拣配
 */

public interface PdaWmsOutPickingService {

    public List pdaRecommend(Map<String, Object> params);
    
    public void pdaPicking(Map<String, Object> params);
    
    public String getLabel(Map<String, Object> params);
}


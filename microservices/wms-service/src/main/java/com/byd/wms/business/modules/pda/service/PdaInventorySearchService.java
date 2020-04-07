package com.byd.wms.business.modules.pda.service;

import java.util.List;
import java.util.Map;

/**
 * 库存查询
 */

public interface PdaInventorySearchService{

    public List queryMatnr(Map<String, Object> params);
    
    public List inventoryList(Map<String, Object> params);
       
}


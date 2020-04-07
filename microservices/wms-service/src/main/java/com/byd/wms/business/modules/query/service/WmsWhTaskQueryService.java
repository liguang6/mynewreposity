package com.byd.wms.business.modules.query.service;

import java.util.Map;
import com.byd.utils.PageUtils;

public interface WmsWhTaskQueryService {

  	PageUtils queryTaskPage(Map<String, Object> params);
 
}


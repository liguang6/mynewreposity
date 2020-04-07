package com.byd.wms.business.modules.in.service;


import com.byd.utils.PageUtils;

import java.util.List;
import java.util.Map;

public interface StoPrintConfigService {
    PageUtils queryList(Map<String,Object> params);
    void edit(Map<String,Object> params);
    void add(Map<String,Object> params);
    void del(Map<String,Object> params);
}


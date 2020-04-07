package com.byd.wms.business.modules.in.dao;

import java.util.List;
import java.util.Map;

public interface StoPrintConfigDao {
    List<Map<String,Object>> queryList(Map<String,Object> params);
    int edit(Map maps);
    int add(Map map);
    int del(Map map);
}

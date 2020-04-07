package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

public interface WmsPostAccountPdaDao {

    int getwhTaskCount(Map<String, Object> params);

    List<Map<String, Object>> getwhTask(Map<String, Object> params);
}

package com.byd.wms.business.modules.report.dao;

import java.util.List;
import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/11/25
 *
 */
public interface MaterialStoreDao {

    //查询收料房库存
    List<Map<String, Object>> selectMaterialList(Map<String, Object> params);

    int selectMaterialInfoCount(Map<String, Object> params);
}

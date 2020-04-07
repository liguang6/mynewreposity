package com.byd.wms.business.modules.report.service;

import com.byd.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * @auther: peng.yang8
 * @data: 2019/11/25
 */
public interface MaterialStoreService {

    PageUtils getMaterialList(Map<String, Object> params);
}

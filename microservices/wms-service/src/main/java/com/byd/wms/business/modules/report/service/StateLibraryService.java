package com.byd.wms.business.modules.report.service;

import com.byd.utils.PageUtils;

import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/12/5
 */

public interface StateLibraryService {

    PageUtils getInventoryList(Map<String, Object> params);
}

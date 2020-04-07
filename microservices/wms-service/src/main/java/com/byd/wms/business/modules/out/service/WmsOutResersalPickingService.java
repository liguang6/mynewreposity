package com.byd.wms.business.modules.out.service;

import com.byd.utils.PageUtils;

import java.util.Map;

/**
 * WMS出库需求拣配
 * @author ren.wei3
 * @date 2019-04-11
 */

public interface WmsOutResersalPickingService {

	public PageUtils queryPage(Map<String, Object> params);


	public Map<String, Object> update(Map<String, Object> params);
}


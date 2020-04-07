package com.byd.wms.business.modules.out.service;

import java.util.Map;

import com.byd.utils.PageUtils;

/**
 * WMS出库需求交接
 * @author ren.wei3
 * @date 2019-04-18
 */
public interface WmsOutHandoverService {

	public PageUtils list(Map<String, Object> params);
	
	public Map<String, Object> handover(Map<String, Object> map);
}

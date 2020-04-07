package com.byd.wms.business.modules.query.service;

import com.byd.utils.PageUtils;

import java.util.Map;


/**
 * @author rain
 * @date 2019年11月21日14:06:07
 * @description 云平台送货单
 */
public interface YPTDeliveryQueryService {

	public PageUtils queryPage(Map<String, Object> params);

	public PageUtils queryPageDetail(Map<String, Object> params);

	public PageUtils queryBarPage(Map<String, Object> params);


}

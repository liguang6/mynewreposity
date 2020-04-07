package com.byd.wms.business.modules.account.service;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.List;
import java.util.Map;

/**
 * STO交货单收货-PDA
 */
public interface WmsPostAccountPdaService {


	PageUtils getwhTask(Map<String, Object> params);


	R posttingAc(Map<String, Object> params);

}

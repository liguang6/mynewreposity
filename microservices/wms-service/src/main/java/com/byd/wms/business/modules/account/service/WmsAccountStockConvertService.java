package com.byd.wms.business.modules.account.service;

import java.util.Map;

import com.byd.utils.R;

/**
 * 411K,412K,309,310,411E,413E等库存类型转移业务处理
 * @author (changsha) thw
 * @date 2019-06-28
 */
public interface WmsAccountStockConvertService{
    
	/**
	 * 库存转移账务处理-411K,412K,309,310,411E,413E等
	 * @param params
	 * @return
	 */
	R save(Map<String, Object> params);
}


package com.byd.web.wms.account.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 411K,412K,309,310,411E,413E等库存类型转移业务处理
 * @author (changsha) thw
 * @date 2019-06-28
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsAccountStockConvertRemote {
    
    /**
     * 库存转移账务处理-411K,412K,309,310,411E,413E等
     * @param params
     * @return
     */
	@RequestMapping(value = "/wms-service/account/stockConvert/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestParam(value="params") Map<String, Object> params) ;
}

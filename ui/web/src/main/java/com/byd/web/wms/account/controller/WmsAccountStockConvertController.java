package com.byd.web.wms.account.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.account.service.WmsAccountStockConvertRemote;

/**
 * 411K,412K,309,310,411E,413E等库存类型转移业务处理
 * @author (changsha) thw
 * @date 2019-06-28
 */
@RestController
@RequestMapping("account/stockConvert")
public class WmsAccountStockConvertController {
	@Autowired
	WmsAccountStockConvertRemote wmsAccountStockConvertRemote;
    
    /**
     * 库存转移账务处理-411K,412K,309,310,411E,413E等
     * @param params
     * @return
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params) {
    	try {
    	   	return wmsAccountStockConvertRemote.save(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
}

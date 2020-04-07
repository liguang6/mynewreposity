package com.byd.web.wms.account.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.account.service.WmsAccountKPORemote;

/**
 * 跨工厂收货账务处理
 * 跨工厂收货，质检合格入库后，采购工厂做收货账务（101），再检查STO定价是否有效，由收货工厂做STO业务的收货联动账务
 * @author (changsha) thw
 * @date 2018-11-19
 */
@RestController
@RequestMapping("account/wmsAccountKPO")
public class WmsAccountKPOController {
	@Autowired
	WmsAccountKPORemote wmsAccountKPORemote;

	@RequestMapping("/listKPOMat")
    public R listKOVMat(@RequestParam Map<String, Object> params) {
		return wmsAccountKPORemote.listKOVMat(params);
	}
	
	
    /**
     * 跨工厂收货（101）账务处理
     * @param params
     * @return
     */
    @RequestMapping("/postGR")
    public R postGR(@RequestParam Map<String, Object> params) {
    	try {
    		return wmsAccountKPORemote.postGR(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
}

package com.byd.web.wms.account.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.account.service.WmsAccountWPORemote;

/**
 * 无PO收货账务，采购补下采购订单后，对应的WMS凭证过账到SAP
 * 处理逻辑：
 * 1 查询无PO收料产生的收货单，将采购补下的PO及行项目号（校验：采购订单行项目未收货数量>=收货单数量）维护到对应的收货单行项目
 * 2 更新收货单关联的103W、105W WMS凭证对应的PO及行项目号，
 * 3 已产生的105W WMS凭证执行SAP过账
 * 4 收货单及关联的103W凭证的BUSINESS_TYPE-WMS业务类型由无PO更新为无PO-补PO：解决部分物料还在质检尚未进仓，后续进仓过账问题
 * @author (changsha) thw
 * @date 2018-11-19
 */
@RestController
@RequestMapping("account/wmsAccountWPO")
public class WmsAccountWPOController {
	@Autowired
	WmsAccountWPORemote wmsAccountWPORemote;
    
    /**
     * 查询无PO收料产生的收货单
     * @param params
     * @return
     */
	@RequestMapping("/listWPOMat")
    public R listWPOMat(@RequestParam Map<String, Object> params) {
    	return wmsAccountWPORemote.listWPOMat(params);
	}
	
    /**
     * 查询采购订单信息
     * @param params
     * @return
     */
	@RequestMapping("/getPoItemInfo")
    public R getPoItemInfo(@RequestParam Map<String, Object> params) {
		return wmsAccountWPORemote.getPoItemInfo(params);
	}
	
	
	
	
    /**
     * 无PO收货账务处理
     * @param params
     * @return
     */
    @RequestMapping("/postGI")
    public R postGI(@RequestParam Map<String, Object> params) {
    	try {
    	   	return wmsAccountWPORemote.postGI(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
}

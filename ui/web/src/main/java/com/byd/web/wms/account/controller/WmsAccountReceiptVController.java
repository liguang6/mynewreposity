package com.byd.web.wms.account.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.account.service.WmsAccountReceiptVRemote;

/**
 * 账务处理-收货V（核销业务虚收）
 * 包含：SAP采购订单收料(V)、工厂间调拨收料（V）、SAP交货单收料（V)、成品入库（V）
 * @author (changsha) thw
 * @date 2018-09-11
 */
@RestController
@RequestMapping("account/wmsAccountReceiptV")
public class WmsAccountReceiptVController {
    @Autowired
    WmsAccountReceiptVRemote wmsAccountReceiptVRemote;
    
    /**
     * SAP采购订单核销信息-保存
     */
    @RequestMapping("/povSave")
    public R povSave(@RequestParam Map<String,Object> params){
        return wmsAccountReceiptVRemote.povSave(params);
    }

    /**
     * SAP采购订单核销信息-修改
     */
    @RequestMapping("/povUpdate")
    public R povUpdate(@RequestParam Map<String,Object> params){
	   	return wmsAccountReceiptVRemote.povUpdate(params);
    }

    /**
     * 删除
     */
    @RequestMapping("/povDelete")
    public R povDelete(@RequestBody Double[] qtys){
    	return wmsAccountReceiptVRemote.povDelete(Arrays.asList(qtys));
    }
    
    /**
     * PO采购订单收货V-核销：
     * 特殊业务：采购订单先过账，以后根据生产情况，再通知供应商送货。
     * 虚收货（V）：用采购订单在系统做收货，产生事务记录，凭证记录（XS101）（过账到SAP,101）
     * @param params
     * @return
     */
    @RequestMapping("/listPOVMat")
    public R listPOVMat(@RequestParam Map<String,Object> params) {
    	return wmsAccountReceiptVRemote.listPOVMat(params);
    } 
    
    /**
     * 根据SAP交货单获取物料列表；
     * 通过调用SAP服务接口，读取SAP交货单数据，如果数据不存在,提示交货单不存在
     * 判断状态：如果交货单对应的状态是已收货，已关闭，已取消，不需要显示交货单数据，提示已收货（或者 已关闭，已取消）
     */
    @RequestMapping("/listDNVMat")
    public R listDNVMat(@RequestParam Map<String, Object> params){
    	return wmsAccountReceiptVRemote.listDNVMat(params);
    }
    
    
    /**
     * 输入303凭证，从SAP及时读取凭证信息，如果没有返回数据，报错提示：凭证号:0000000105不存在 ；
     * 校验对应凭证移动类型是否是303，如果不是报错提示：凭证号:0000000105不是有效的303凭证。
     * 核对凭证接收工厂和当前账号工厂的权限是否一致，如果不一致报错提示：您无权操作***工厂的凭证；
     * 校验凭证接收工厂对应工厂配置表是否启用核销【WMS_C_PLANT】-- HX_FLAG ，如果没有启用，提示：凭证号0000000105 接收工厂没有启用核销业务！，如果启用核销，允许带出凭证数据
     */
    @RequestMapping("/listTOVMat")
    public R listTOVMat(@RequestParam Map<String, Object> params){
    	return wmsAccountReceiptVRemote.listTOVMat(params);
    }
    
    /**
     * SAP生产订单收货过账(V)、副产品收货过账(V)-核销：
     * 特殊业务：生产订单先过账，以后根据实际生产情况，再实物收货入库（A）。
     * 虚收货（V）：用生产订单在系统做收货（包含主产品，联产品、副产品），产生事务记录，凭证记录（XS101M）（过账到SAP,101）
     * @param params
     * @return
     */
    @RequestMapping("/listMOVMat")
    public R listMOVMat(@RequestParam Map<String,Object> params) {
    	return wmsAccountReceiptVRemote.listMOVMat(params);
    } 
    
    
    /**
     * 收货（V）确认-核销业务
     * @param params
     * @return
     */
    @RequestMapping("/boundIn")
    public R boundIn(@RequestParam Map<String, Object> params) {
    	try {
    	   	return wmsAccountReceiptVRemote.boundIn(params);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}

    }
    
}

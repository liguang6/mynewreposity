package com.byd.web.wms.cswlms.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.web.wms.cswlms.service.WmsDispatchingBillPickingRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年1月19日 上午10:19:42 
 * 类说明 
 */
@RestController
@RequestMapping("/cswlms/dispatchingBillPicking")
public class DispatchingBillPickingController {
	@Autowired
	private WmsDispatchingBillPickingRemote wmsDispatchingBillPickingRemote;
    @Autowired
    private RedisUtils redisUtils;
	
	@RequestMapping("/listJIS")
    public R listJIS(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listJIS(params);
	}
	
	@RequestMapping("/listJISDetail")
    public R listJISDetail(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listJISDetail(params);
	}
	
	@RequestMapping("/listAssembly")
    public R listAssembly(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listAssembly(params);
	}
	
	/**
	 * 拣配下架
	 * @param params
	 * @return
	 */
	@RequestMapping("/picking")
    public R picking(@RequestParam Map<String, Object> params){
		
		return wmsDispatchingBillPickingRemote.picking(params);
	}
	
	@RequestMapping("/listFeiJIS")
    public R listFeiJIS(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listFeiJIS(params);
	}
	
	@RequestMapping("/feijispicking")
    public R feijispicking(@RequestParam Map<String, Object> params){
		
		return wmsDispatchingBillPickingRemote.feijispicking(params);
	}
	
	@RequestMapping("/printjis")
    public R printJIS(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.printjis(params);
	}
	
	@RequestMapping("/printfeijis")
    public R printfeiJIS(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.printfeijis(params);
	}
	
	@RequestMapping("/checkJISDetailStatus")
    public R checkJISDetailStatus(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.checkJISDetailStatus(params);
	}
	
	@RequestMapping("/checkQueRen")
    public R checkQueRen(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.checkQueRen(params);
	}
	
	@RequestMapping("/listQueRen")
    public R listQueRen(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listQueRen(params);
	}
	
	@RequestMapping("/updateQueRen")
    public R updateQueRen(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.updateQueRen(params);
	}
	
	@RequestMapping("/listjiaojie")
    public R listjiaojie(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listjiaojie(params);
	}
	
	@RequestMapping("/dispatchingHandover")
    public R dispatchingHandover(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.dispatchingHandover(params);
	}
	
	@RequestMapping("/listfabu")
    public R listfabu(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listfabu(params);
	}
	
	@RequestMapping("/updatefabu")
    public R updatefabu(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.updatefabu(params);
	}
	@RequestMapping("/dispatchingchaif")
    public R dispatchingchaif(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.dispatchingchaif(params);
	}
	
	@RequestMapping("/listwmlsException")
    public R listwmlsException(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listwmlsException(params);
	}
	
	@RequestMapping("/listPickRecordNoCount")
    public R listPickRecordNoCount(@RequestParam Map<String, Object> params){
		return wmsDispatchingBillPickingRemote.listPickRecordNoCount(params);
	}
}

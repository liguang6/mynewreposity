package com.byd.web.wms.in.controller;

import java.util.Map;

import com.byd.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.common.service.WmsSapRemote;
import com.byd.web.wms.in.service.WmsInReceiptRemote;

/**
 * WMS收货单
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-20 16:06:38
 */
@RestController
@RequestMapping("in/wmsinreceipt")
public class WmsInReceiptController {
	@Autowired
	WmsInReceiptRemote wmsInReceiptRemote;
	@Autowired
	WmsSapRemote wmsSapRemote;
    @Autowired
    private UserUtils userUtils;
	
    /**
     * 根据SCM送货单获取物料列表；
     * 判断状态：如果送货单对应的状态是已收货，已关闭，已取消，不需要显示送货单数据，提示已收货（或者 已关闭，已取消）
     * 读取SCM送货单数据和包装箱数据，如果数据不存在,提示送货单不存在
     */
    @RequestMapping("/listScmMat")
    public R listScmMat(@RequestParam Map<String, Object> params){
		//ModBy:YK 190402 统一WEB和APP 用户信息和权限获取代码移动到business-service    	
    	return wmsInReceiptRemote.listScmMat(params);
    }

    /**
     * 手动同步生产订单数据@YK180823
     */
    @RequestMapping("/syncProdordDetail")
    public R syncProdordDetail(@RequestParam Map<String, Object> params){
    	System.out.println("-->WmsInReceiptController:syncProdordDetail");
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	Map<String, Object> result = wmsSapRemote.getSapBapiProdordDetailSync(params);
    	return R.ok(result.get("MESSAGE").toString());
    }
    
    /**
     * 手动同步采购订单数据@YK180824
     */
    @RequestMapping("/syncPoDetail")
    public R syncPoDetail(@RequestParam Map<String, Object> params){
    	System.out.println("-->WmsInReceiptController:syncPoDetail");
    	Map<String, Object> result = wmsSapRemote.getSapBapiPoDetailSync(params);
    	return R.ok(result.get("MESSAGE").toString());
    }
    
    /**
     * 手动同步物料数据@YK180905
     */
    @RequestMapping("/syncMatDetail")
    public R syncMatDetail(@RequestParam Map<String, Object> params){
    	System.out.println("-->WmsInReceiptController:syncMatDetail");
    	Map<String, Object> result = wmsSapRemote.getSapBapiMaterialInfoSync(params);
    	return R.ok(result.get("MESSAGE").toString());
    }
    
    /**
     * 获取包装箱信息
     * @param params
     * @return
     */
    @RequestMapping("/listSKInfo")
    public R listSKInfo(@RequestParam Map<String, Object> params) {
    	return wmsInReceiptRemote.listSKInfo(params);
    }
    
    /**
     * PO采购订单收货：
     * 供应商根据采购订单数据，手工开送货单并送货到收料房，收料员收货后，用采购订单在系统做收货，产生事务记录，
     * 凭证记录（103）（过账到SAP），收料房库存，送检单，创建收货单，包装信息。
     * 根据工厂配置是否启用核销，如果启用核销还需判断采购订单行项目核销数据表是否有 还需核销数量，
     * 如果有，需要先做核销的实物收货，冲销核销的虚收数量。工厂配置不启用核销，就不需要检查核销数据。
     * @param params
     * @return
     */
    @RequestMapping("/listPOMat")
    public R listPOMat(@RequestParam Map<String,Object> params) {
    	return wmsInReceiptRemote.listPOMat(params);
    } 
    
    /**
     * SAP交货单收货：
     * @param params
     * @return
     */
    @RequestMapping("/listOutMat")
    public R listOutMat(@RequestParam Map<String,Object> params) {
    	return wmsInReceiptRemote.listOutMat(params);
    }
    
    /**
     * 跨工厂采购订单收货
     * @param params
     * @return
     */
    @RequestMapping("/listPOCFMat")
    public R listPOCFMat(@RequestParam Map<String,Object> params) {
    	return wmsInReceiptRemote.listPOCFMat(params);
    } 
    
    /**
     * 303调拨收货
     * @param params
     * @return
     */
    @RequestMapping("/list303Mat")
    public R list303Mat(@RequestParam Map<String, Object> params) {
    	return wmsInReceiptRemote.list303Mat(params);

    }
    /**
     * 303调拨收货
     * @param params
     * @return
     */
    @RequestMapping("/list303AMat")
    public R  list303AMat(@RequestParam Map<String, Object> params) {
    	return wmsInReceiptRemote.list303AMat(params);

    }
    
    /**
     * SAP采购订单收料（A）
     * @param params
     * @return
     */
    @RequestMapping("/listPOAMat")
    public R  listPOAMat(@RequestParam Map<String, Object> params) {
    	return wmsInReceiptRemote.listPOAMat(params);

    }
    
    /**
     * SAP交货单收料（A）
     * @param params
     * @return
     */
    @RequestMapping("/listOutAMat")
    public R  listOutAMat(@RequestParam Map<String, Object> params) {
    	return wmsInReceiptRemote.listOutAMat(params);

    }
    
    /**
     * 收货确认
     * @param params
     * @return
     */
    @RequestMapping("/boundIn")
    public R boundIn(@RequestParam Map<String, Object> params) {
    	return wmsInReceiptRemote.boundIn(params);
    }
    
    /**
     * 根据物料号获取物料信息（行文本、物料描述、是否紧急物料、是否危化品）
     * @param params
     * @return
     */
    @RequestMapping("/getMatInfo")
    public R getMatInfo(@RequestParam Map<String, Object> params) {
    	return wmsInReceiptRemote.getMatInfo(params);
    }

    @RequestMapping("/info/{qty}")
    public R info(@PathVariable("qty") Long qty) {
    	return wmsInReceiptRemote.info(qty);
    }
    
    /**
     * 云平台送货单获取数据
     * @param params
     * @return
     */
    @RequestMapping("/listCloudMat")
    public R listCloudMat(@RequestParam Map<String, Object> params){ 	
    	return wmsInReceiptRemote.listCloudMat(params);
    }

    /**
     * 根据工厂和物料号获取SAP物料主数据信息
     */
    @RequestMapping("/listSAPMatDetail")
    public R listMatDetail(@RequestParam Map<String, Object> params){

        return wmsInReceiptRemote.getSAPMatDetail(params);
    }
    /**
     * 查询事务记录
     */
    @RequestMapping("/exportExcel")
    public R list(@RequestParam Map<String, Object> params){
        return wmsInReceiptRemote.exportExcel(params);

    }
}

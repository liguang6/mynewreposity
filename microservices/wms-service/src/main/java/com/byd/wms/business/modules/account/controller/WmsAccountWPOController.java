package com.byd.wms.business.modules.account.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.account.service.WmsAccountWPOService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;

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
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	WmsSapRemote wmsSapRemote;
    @Autowired
    WmsCTxtService wmsCTxtService;
    @Autowired
    WmsAccountWPOService wmsAccountWPOService;
    @Autowired
    WmsInReceiptService wmsInReceiptService;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 查询无PO收料产生的收货单
     * @param params
     * @return
     */
	@RequestMapping("/listWPOMat")
    public R listWPOMat(@RequestBody Map<String, Object> params) {
   		Map<String,Object> currentUser = userUtils.getUser();
   		params.put("USERNAME", currentUser.get("USERNAME"));
   		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
   		params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		
    	String WERKS = params.get("WERKS").toString();//工厂
    	/**
    	 * 判断账号是否有对应工厂的操作权限，如果没有提示无权限
    	 */
    	Set<Map<String,Object>> deptList= userUtils.getUserWerks("ACCOUNT_WPO");
    	boolean auth=false;
    	for(Map dept:deptList) {
    		if(dept.get("CODE").equals(WERKS)) {
    			auth=true;
    			break;
    		}
    	}
    	if(!auth) {
    		return R.error("您无权操作"+WERKS+"工厂的数据权限！");
    	}
    	/**
    	 * 根据工厂、仓库号、供应商代码、收货日期查询收货单信息
    	 */
    	List<Map<String, Object>> receiptList = wmsAccountWPOService.getWPOReceiptInfo(params);
    	
		return R.ok().put("result", receiptList);
	}
	
    /**
     * 查询采购订单信息
     * @param params
     * @return
     */
	@RequestMapping("/getPoItemInfo")
    public R getPoItemInfo(@RequestParam Map<String, Object> params) {
    	/**
    	 * 根据采购订单号获取采购订单行项目信息
    	 */
    	List<Map<String, Object>> poItemInfoList = wmsAccountWPOService.getPoItemInfo(params);
    	
		return R.ok().put("result", poItemInfoList);
	}
	
	
	
	
    /**
     * 无PO收货账务处理
     * @param params
     * @return
     */
    @RequestMapping("/postGI")
    public R postGI(@RequestParam Map<String, Object> params) {
    	try {
       			Map<String,Object> currentUser = userUtils.getUser();
       			params.put("USERNAME", currentUser.get("USERNAME"));
       			params.put("FULL_NAME", currentUser.get("FULL_NAME"));
       			params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
       			params.put("EDITOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    			
		    	String matListStr=params.get("matList").toString();
				String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
				
		    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
		    	List<String> asnList=new ArrayList<String>(); //统计已收数量用的条件（采购订单号#*采购订单行项目号）
		    	Map<String,Double> pagePoItemQtyMap = new HashMap<String,Double>(); //记录页面选择的采购订单及行项目关联的无PO收料数量（采购订单号#*料号#*采购订单行项目号）
		    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
		    		m=(Map<String,Object>)m;
		    		m.put("CREATOR", params.get("CREATOR").toString());
		    		m.put("CREATE_DATE", curDate);
		    		m.put("EDITOR", params.get("EDITOR").toString());
		    		m.put("EDIT_DATE", curDate);
		    		m.put("WERKS", params.get("WERKS"));
		    		m.put("WH_NUMBER", params.get("WH_NUMBER"));
		    		
		    		matList.add(m);
		    		String poStr = m.get("PO_NO")+"#*"+m.get("PO_ITEM_NO");
		    		asnList.add(poStr);
		    		
		    		Double qty = Double.valueOf("0");
		    		if(pagePoItemQtyMap.get(poStr) !=null) {
		    			qty = pagePoItemQtyMap.get(poStr);
		    		}
		    		qty += Double.parseDouble(m.get("PATCH_QTY").toString());
		    		pagePoItemQtyMap.put(poStr, qty);
		    	});
		    	//校验填写的采购订单、行项目数量是否满足
		    	/**
		    	 * 根据封装好的查询条件（SAP采购订单号##订单行项目号）查询每条行项目已收货数量, 可收货数量
		    	 */    	
		    	Map<String,Object> rmap=new HashMap<String,Object>();
		    	rmap.put("BILL_TYPE", "PO_NO");
		    	rmap.put("asnList", asnList);
		    	Map<String,Object> receiptCount=wmsInReceiptService.getReceiptCount(rmap);
		    	String errStr = "";
		    	for (Map<String,Object> map : matList) {
		    		String poStr = map.get("PO_NO")+"#*"+map.get("MATNR")+"#*"+map.get("PO_ITEM_NO");
			    	String s = (String) receiptCount.get(poStr);
		    		if (s==null) {
		    			s="0";
		    		}
		    		double finishedQty = 0;//采购订单已收数量
		    		finishedQty = Double.parseDouble(s);
		    		double MAX_MENGE = Double.parseDouble(map.get("MAX_MENGE").toString()); //采购订单行项目最大可收货数量
		    		double pagePoItemQty = pagePoItemQtyMap.get(map.get("PO_NO")+"#*"+map.get("PO_ITEM_NO"));
		    		
		    		if(MAX_MENGE-finishedQty-pagePoItemQty<0) {
		    			//PO行项目数量不够
		    			errStr +="采购订单("+map.get("PO_NO")+")行项目("+map.get("PO_ITEM_NO")+")可收货数量不足！";
		    		}
				}
		    	if(errStr !=null && errStr.length()>0) {
		    		return R.error(errStr);
		    	}
		    	
		    	params.put("matList", matList);
				return wmsAccountWPOService.postGI_WPO(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
}

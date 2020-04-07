package com.byd.wms.business.modules.account.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.account.service.WmsAccountKPOService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;

/**
 * 跨工厂收货账务处理
 * 跨工厂收货，质检合格入库后，采购工厂做收货账务（101），再检查STO定价是否有效，由收货工厂做STO业务的收货联动账务
 * @author (changsha) thw
 * @date 2018-11-19
 */
@RestController
@RequestMapping("account/wmsAccountKPO")
public class WmsAccountKPOController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	WmsSapRemote wmsSapRemote;
    @Autowired
    WmsCTxtService wmsCTxtService;
    @Autowired
    WmsAccountKPOService wmsAccountKPOService;
    @Autowired
    private UserUtils userUtils;

	@RequestMapping("/listKPOMat")
    public R listKOVMat(@RequestBody Map<String, Object> params) {
       	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		
	   	String WERKS = params.get("WERKS").toString();
    	String GR_WERKS = params.get("GR_WERKS").toString();//PO工厂
    	String WH_NUMBER = params.get("WH_NUMBER").toString();//仓库号
    	String PO_NO = params.get("PO_NO")==null?null:params.get("PO_NO").toString();//采购订单号
    	String LIFNR = params.get("LIFNR")==null?null:params.get("LIFNR").toString();//供应商
    	String BEDNR = params.get("BEDNR")==null?null:params.get("BEDNR").toString();//需求跟踪号
    	String MATNR = params.get("MATNR")==null?null:params.get("MATNR").toString();//料号
    	String CREATE_DATE_S = params.get("CREATE_DATE_S")==null?null:params.get("CREATE_DATE_S").toString();//收货日期
    	String CREATE_DATE_E = params.get("CREATE_DATE_E")==null?null:params.get("CREATE_DATE_E").toString();
    	
    	params.put("WERKS", WERKS);
    	params.put("GR_WERKS", GR_WERKS);
    	params.put("WH_NUMBER", WH_NUMBER);
    	params.put("PO_NO", PO_NO);
    	params.put("LIFNR", LIFNR);
    	params.put("BEDNR", BEDNR);
    	params.put("MATNR", MATNR);
    	params.put("CREATE_DATE_S", CREATE_DATE_S);
    	params.put("CREATE_DATE_E", CREATE_DATE_E);
    	
    	/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
    	Set<Map<String,Object>> deptList= userUtils.getUserWerks("ACCOUNT_KPO");
    	boolean auth=false;
    	for(Map dept:deptList) {
    		if(dept.get("CODE").equals(WERKS)) {
    			auth=true;
    			break;
    		}
    	}
    	if(!auth) {
    		return R.error("您无权操作"+WERKS+"工厂的采购订单！");
    	}
    	
    	List<Map<String, Object>> wmsDocList_105DS = wmsAccountKPOService.getKPOWmsDocInfo(params);

		
		return R.ok().put("result", wmsDocList_105DS);
	}
	
	
    /**
     * 跨工厂收货（101）账务处理
     * @param params
     * @return
     */
    @RequestMapping("/postGR")
    public R postGR(@RequestBody Map<String, Object> params) {
    	try {
           		Map<String,Object> currentUser = userUtils.getUser();
           		params.put("USERNAME", currentUser.get("USERNAME"));
           		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
           		params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    		
    	    	String matListStr=params.get("matList").toString();
    			String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
    			
    	    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    	    		m=(Map<String,Object>)m;
    	    		m.put("CREATOR", params.get("CREATOR").toString());
    	    		m.put("CREATE_DATE", curDate);
    	    		m.put("WERKS", params.get("WERKS"));
    	    		m.put("WH_NUMBER", params.get("WH_NUMBER"));
    	    		m.put("RECEIPT_QTY", m.get("QTY_SAP_101"));
    	    		m.put("REVERSAL_FLAG", "X");
    	    		m.put("CANCEL_FLAG", "X");
    	    		
    	    		matList.add(m);
    	    	});
    	    	params.put("matList", matList);
    			
    			return wmsAccountKPOService.postGR_101(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
}

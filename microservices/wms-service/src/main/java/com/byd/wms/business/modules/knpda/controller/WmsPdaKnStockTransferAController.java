package com.byd.wms.business.modules.knpda.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.account.service.WmsAccountStockConvertService;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.knpda.service.WmsPdaKnCommonService;
import com.byd.wms.business.modules.knpda.service.WmsPdaKnLableRecordService;

/*411K业务
 * @author chen.yafei
 * @email chen.yafei1@byd.com
 * @date 2019-12-11
 */

@RestController
@RequestMapping("knpda/stockTransferA")
public class WmsPdaKnStockTransferAController {
	
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsPdaKnCommonService wmsPdaKnCommonService;
	
	@Autowired
    private UserUtils userUtils;
	//根据条码查询 标签物料信息
	@CrossOrigin
	@RequestMapping("/list")
    public R list(@RequestBody Map<String, Object> params){
        try {
        	PageUtils page =wmsPdaKnCommonService.queryLableList(params);
    		int boxs = commonService.getLabelCacheInfoCount(params);
            return R.ok().put("page", page).put("boxs", boxs);
		} catch(Exception e) {
    		return R.error(e.getMessage());
    	}
    }
	
	@CrossOrigin
	@RequestMapping("/labelList")
    public R labelList(@RequestBody Map<String, Object> params){
		try {
			PageUtils page = wmsPdaKnCommonService.getLableInfoList(params);
			return R.ok().put("page", page);
		} catch(Exception e) {
    		return R.error(e.getMessage());
    	}	
		
        		
    }
	
	@CrossOrigin
	@RequestMapping("/deleteLabel")
    public R deleteLabel(@RequestBody Map<String, Object> params){
		try {
			wmsPdaKnCommonService.deleteLabelList(params);	
		} catch(Exception e) {
    		return R.error(e.getMessage());
    	}	
		return R.ok();
    }
	
	//账务处理
	@CrossOrigin
	@RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
		
		
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		params.put("EDITOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		
    	String matListStr=params.get("matList").toString();
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
		
		params.put("CREATE_DATE", curDate);
		params.put("EDIT_DATE", curDate);
		JSONObject label_lgort=new JSONObject();
		List<Map<String,Object>> logList=new ArrayList<Map<String,Object>>();
    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    		m=(Map<String,Object>)m;
    		m.remove("BOX_QTY");
    		m.put("CREATOR", params.get("CREATOR").toString());
    		m.put("CREATE_DATE", curDate);
    		m.put("EDITOR", params.get("EDITOR").toString());
    		m.put("EDIT_DATE", curDate);
    		m.put("WERKS", params.get("WERKS"));
    		m.put("WH_NUMBER", params.get("WH_NUMBER"));
    		m.put("BUSINESS_NAME", "411_K");
    		
    		String f_lgort=m.get("F_LGORT").toString();    		
    		JSONObject target=new JSONObject(); 
    		target.put("F_LGORT", f_lgort);
    		String label=m.get("LABEL").toString();
    		String[] labelList=label.trim().split(",");
    		for (int i=0;i<labelList.length;i++) {	
    			label_lgort.put(labelList[i], target);
    		}
    		logList.add(m);
    	});
    	params.put("label_lgort", label_lgort);
    	
    	params.put("logList", logList);
    	
    	try {
    		Map<String, Object> retMap = wmsPdaKnCommonService.save(params);
    		String WMS_NO = retMap.get("WMS_NO").toString();
    		String SAP_DOC_NO = retMap.get("SAP_DOC_NO").toString();
    		String msg = retMap.get("msg").toString();
			return R.ok().put("WMS_NO", WMS_NO)
					.put("SAP_DOC_NO", SAP_DOC_NO)
					.put("msg", msg);
		} catch(Exception e) {
    		return R.error(e.getMessage());
    	}
	    	
	
    }

}

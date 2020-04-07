package com.byd.wms.business.modules.account.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.account.service.WmsAccountStockConvertService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;

/**
 * 411K,412K,309,310,411E,413E等库存类型转移业务处理
 * @author (changsha) thw
 * @date 2019-06-28
 */
@RestController
@RequestMapping("account/stockConvert")
public class WmsAccountStockConvertController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	WmsSapRemote wmsSapRemote;
    @Autowired
    WmsCTxtService wmsCTxtService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private WmsAccountStockConvertService wmsAccountStockConvertService;
    
    /**
     * 库存转移账务处理-411K,412K,309,310,411E,413E等
     * @param params
     * @return
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params) {
    	try {
	   			Map<String,Object> currentUser = userUtils.getUser();
	   			params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
	   			params.put("EDITOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
				
		    	String matListStr=params.get("matList").toString();
				String curDate=DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
				
				params.put("CREATE_DATE", curDate);
				params.put("EDIT_DATE", curDate);
				
		    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
		    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
		    		m=(Map<String,Object>)m;
		    		m.put("CREATOR", params.get("CREATOR").toString());
		    		m.put("CREATE_DATE", curDate);
		    		m.put("EDITOR", params.get("EDITOR").toString());
		    		m.put("EDIT_DATE", curDate);
		    		m.put("WERKS", params.get("WERKS"));
		    		m.put("WH_NUMBER", params.get("WH_NUMBER"));
		    		
		    		matList.add(m);
		    	});
		    	
		    	params.put("matList", matList);
				return wmsAccountStockConvertService.save(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
	}

}

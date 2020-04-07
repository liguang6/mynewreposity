package com.byd.zzjmes.modules.produce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.byd.zzjmes.modules.produce.service.OutsourcingService;

/**
 * 委外发货
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("outsourcing")
public class OutsourcingController {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OutsourcingService outsourcingService;
    @Autowired
    private UserUtils userUtils;
    
    /**
   	 * 根据工厂、车间、线别、订单编号、批次编号、零部件号、工序获取下料明细信息及上工序产量信息
   	 * @param condMap 
   	 * 	"werks":"workshop": "line": "order_no": "zzj_plan_batch": 
   		"zzj_no": "process_name" 工序编号  machine_plan_items_id
   	 * @return
   	 */
    @RequestMapping("/getOutsourcingMatInfo")
 	public R getOutsourcingMatInfo(@RequestParam Map<String,Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("FULL_NAME", user.get("FULL_NAME"));
 		return R.ok().put("data", outsourcingService.getOutsourcingMatInfo(params));
 	}
    
    /**
     * 委外发货保存
     * @param params
     * @return
     */
    @RequestMapping("/saveOutsourcing")
  	public R saveOutsourcing(@RequestBody Map<String,Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("editor", user.get("USERNAME")+":"+user.get("FULL_NAME"));
    	params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	
    	String matListStr = params.get("matInfoList").toString();
    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	int i = 1;
    	for (Map m:JSONObject.parseArray(matListStr, Map.class)) {
    		m.put("werks", params.get("werks"));
    		m.put("workshop", params.get("workshop"));
    		m.put("line", params.get("line"));
    		m.put("plan_date", params.get("business_date"));
    		m.put("plan_process", m.get("outsourcing_process_name"));
    		m.put("plan_quantity", m.get("outsourcing_quantity"));
    		m.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
    		m.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		m.put("creator", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			m.put("creat_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			m.put("create_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		m.put("item_no", i);
    		
    		i++;
    		
    		matList.add(m);
    	}
    	params.put("matInfoList", matList);
    	Map rtn = outsourcingService.saveOutsourcingInfo(params);
    	if(StringUtils.isEmpty(rtn.get("msg").toString())) {
    		matList = (List<Map<String,Object>>)rtn.get("matInfoList");
    		return R.ok().put("matList", matList).put("headInfo", params);
    	}else {
    		return R.error(rtn.get("msg").toString());
    	}
  		
  	}
    
}
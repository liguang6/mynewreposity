package com.byd.zzjmes.modules.produce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.produce.service.MatHandoverService;

/**
 * 车间内交接
 * @author cscc tangj
 * @email 
 * @date 2019-09-23 16:16:08
 */
@RestController
@RequestMapping("matHandover")
public class MatHandoverController {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MatHandoverService matHandoverService;
    @Autowired
    private UserUtils userUtils;
    
    /**查询分页**/
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
    	PageUtils page = matHandoverService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    @RequestMapping("/getMatInfo")
 	public R getMatInfo(@RequestParam Map<String,Object> params){
    	
 		return R.ok().put("data", matHandoverService.getMatInfo(params));
 	}
    @RequestMapping("/getSupplyMatInfo")
 	public R getSupplyMatInfo(@RequestParam Map<String,Object> params){
    	
 		return R.ok().put("data", matHandoverService.getSupplyMatInfo(params));
 	}
    /**
     * 保存
     * @param params
     * @return
     */
    @RequestMapping("/save")
  	public R save(@RequestBody Map<String,Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("editor", user.get("USERNAME")+":"+user.get("FULL_NAME"));
    	params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	
    	String matListStr = params.get("matInfoList").toString();
    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	for (Map m:JSONObject.parseArray(matListStr, Map.class)) {
    		matList.add(m);
    	}
    	params.put("matInfoList", matList);
    	
    	try {
    		matHandoverService.save(params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return R.error("保存失败！" + e.getMessage());
		}
		return R.ok();
  	
  	}
    /**
     * 保存车间供货
     * @param params
     * @return
     */
    @RequestMapping("/saveSupply")
  	public R saveSupply(@RequestBody Map<String,Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("editor", user.get("USERNAME")+":"+user.get("FULL_NAME"));
    	params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	
    	String matListStr = params.get("matInfoList").toString();
    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	for (Map m:JSONObject.parseArray(matListStr, Map.class)) {
    		matList.add(m);
    	}
    	params.put("matInfoList", matList);
    	
    	try {
    		matHandoverService.saveSupply(params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return R.error("保存失败！" + e.getMessage());
		}
		return R.ok();
  	
  	}
    
    @RequestMapping("/getList")
    public R getList(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list=matHandoverService.getList(params);
		return new R().put("data", list);
	}
    
    /**车间供货查询分页**/
    @RequestMapping("/querySupplyPage")
    public R querySupplyPage(@RequestParam Map<String, Object> params){
    	PageUtils page = matHandoverService.querySupplyPage(params);
        return R.ok().put("page", page);
    }
    
    @RequestMapping("/getSupplyDetailList")
    public R getSupplyDetailList(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list=matHandoverService.getSupplyDetailList(params);
		return new R().put("data", list);
	}
    
    @RequestMapping("/getHandoverRuleList")
    public R getHandoverRuleList(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list=matHandoverService.getHandoverRuleList(params);
		return new R().put("data", list);
	}
}
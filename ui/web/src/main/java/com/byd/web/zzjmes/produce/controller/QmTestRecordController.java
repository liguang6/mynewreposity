package com.byd.web.zzjmes.produce.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.masterdata.service.MasterDataRemote;
import com.byd.web.zzjmes.produce.service.BatchPlanRemote;
import com.byd.web.zzjmes.produce.service.QmTestRecordRemote;

/**
 * 品质检验
 * @author tangj
 * @email 
 * @date 2019-09-16 16:16:08
 */
@RestController
@RequestMapping("zzjmes/qmTestRecord")
public class QmTestRecordController {
    @Autowired
    private QmTestRecordRemote qmTestRecordRemote;
    
    @Autowired
    private MasterDataRemote masterDataRemote;
    @Autowired
    private UserUtils userUtils;
    /**查询分页**/
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
		params.put("login_user", user.get("USERNAME")+"："+user.get("FULL_NAME"));
        return qmTestRecordRemote.queryPage(params);
    }
    @RequestMapping("/getTestRules")
 	public R getTestRules(@RequestParam Map<String,Object> params){
    	
    	List<Map<String,Object>> orderList=qmTestRecordRemote.getOrderList(params);
    	if(orderList.size()==0) {
    		return R.error(params.get("order_no")+":订单信息不存在！");
    	}
    	String orderAreaCode=orderList.get(0).get("order_area_code")!=null ?
    			orderList.get(0).get("order_area_code").toString().trim() : "";
    	if(orderAreaCode.equals("")) {
    		return R.error(params.get("order_no")+":订单区域代码未维护！");
    	}
    	/**************判断是否已存在*********/
    	R r=qmTestRecordRemote.getTestRecordList(params);
    	if(r.get("product_data")!=null) {
        	List<Map<String,Object>> product_data= 
        			(List<Map<String, Object>>) r.get("product_data");
              if(product_data.size()>0) {
            	  return R.error(params.get("zzj_no")+":已录入生产自检数据,不能重复录入！");
              }
    	}
    	/**********************************/
    	
    	params.put("order_area_code", orderAreaCode);
    	// 获取该零部件号的需求数量
    	List<Map<String,Object>> pmdList=qmTestRecordRemote.getPmdInfo(params);
    	
    	return masterDataRemote.getQmsTestRules(params).put("pmdList", pmdList);
 	}
    
    @RequestMapping("/getQmsTestToolList")
  	public R getQmsTestToolList(@RequestParam Map<String,Object> params){
  		return masterDataRemote.getQmsTestToolList(params);
  	}
    
    
	@RequestMapping("/getQmsTestStandard")
	public R getQmsTestStandard(@RequestParam Map<String,Object> params) {
		return masterDataRemote.getQmsTestStandard(params);
	}

	@RequestMapping("/save")
	public R save(@RequestParam Map<String,Object> params) {
		String result_type=params.get("result_type").toString();
		Map<String,Object> user = userUtils.getUser();
		if(result_type.equals("0")) {
			params.put("product_editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			params.put("product_edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		}
		if(result_type.equals("1")) {
			params.put("qc_editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			params.put("qc_edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		}
		
		return qmTestRecordRemote.save(params);
	}
	@RequestMapping("/del")
  	public R del(@RequestParam Map<String,Object> params){
  		return qmTestRecordRemote.del(params);
  	}
    @RequestMapping("/getTestRecordList")
  	public R getTestRecordList(@RequestParam Map<String,Object> params){
    	return qmTestRecordRemote.getTestRecordList(params);
  	}
	
	@RequestMapping("/getQcTestRules")
 	public R getQcTestRules(@RequestParam Map<String,Object> params){
    	
    	List<Map<String,Object>> orderList=qmTestRecordRemote.getOrderList(params);
    	if(orderList.size()==0) {
    		return R.error(params.get("order_no")+":订单信息不存在！");
    	}
    	String orderAreaCode=orderList.get(0).get("order_area_code")!=null ?
    			orderList.get(0).get("order_area_code").toString().trim() : "";
    	if(orderAreaCode.equals("")) {
    		return R.error(params.get("order_no")+":订单区域代码未维护！");
    	}
    	
    	params.put("order_area_code", orderAreaCode);
    	
    	// 获取 生产自检录入数据
    	R r=qmTestRecordRemote.getTestRecordList(params);
    	if(r.get("product_data")!=null) {
        	List<Map<String,Object>> product_data= 
        			(List<Map<String, Object>>) r.get("product_data");
              if(product_data.size()==0) {
            	  return R.error(params.get("zzj_no")+":生产自检未录入数据！");
              }
    	}
    	if(r.get("qc_data")!=null) {
        	List<Map<String,Object>> qc_data= 
        			(List<Map<String, Object>>) r.get("qc_data");
              if(qc_data.size()>0) {
            	  return R.error(params.get("zzj_no")+":品质检验已录入数据，不能重复录入！");
              }
    	}
    	// 获取该零部件号的需求数量
    	List<Map<String,Object>> pmdList=qmTestRecordRemote.getPmdInfo(params);
    	r.putAll(masterDataRemote.getQmsTestRules(params).put("pmdList", pmdList));
    	return r;
 	}
}
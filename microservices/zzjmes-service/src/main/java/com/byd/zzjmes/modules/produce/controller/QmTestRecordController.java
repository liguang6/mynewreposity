package com.byd.zzjmes.modules.produce.controller;

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

import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.order.service.OrderService;
import com.byd.zzjmes.modules.produce.service.BatchPlanService;
import com.byd.zzjmes.modules.produce.service.QmTestRecordService;

/**
 * 自制件品质检验
 * @author tangj
 * @email 
 * @date 2019-09-16 10:12:08
 */
@RestController
@RequestMapping("qmTestRecord")
public class QmTestRecordController {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserUtils userUtils;
    @Autowired
	private QmTestRecordService qmTestRecordService;
    // 查询订单 
    @RequestMapping("/getOrderList")
 	public List<Map<String,Object>> getOrderList(@RequestParam Map<String,Object> params){
    	
    	List<Map<String,Object>> orderList=qmTestRecordService.getOrderList(params);
 		return orderList;
 	}
    
    @RequestMapping("/getPmdInfo")
  	public List<Map<String,Object>> getPmdInfo(@RequestParam Map<String,Object> params){
  		return qmTestRecordService.getPmdInfo(params);
  	}
    
    
	@RequestMapping("/save")
	public R save(@RequestParam Map<String,Object> params) {
		Map<String,Object> user = userUtils.getUser();

		params.put("edit_date",DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		params.put("editor", user.get("USERNAME")+":"+user.get("FULL_NAME"));

		try {
		    qmTestRecordService.save(params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return R.error("保存失败！" + e.getMessage());
		}
		return R.ok();
	}

	@RequestMapping("/queryPage")
	public R queryPage(@RequestParam Map<String,Object> params) {
		PageUtils page = qmTestRecordService.queryPage(params);
        return R.ok().put("page", page);
	}

	@RequestMapping("/del")
	public R del(@RequestParam Map<String,Object> params) {
		try {
		    qmTestRecordService.del(params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return R.error("删除失败！" + e.getMessage());
		}
        return R.ok();
	}
	
    @RequestMapping("/getTestRecordList")
  	public R getTestRecordList(@RequestParam Map<String,Object> params){
    	
    	List<Map<String,Object>> productList=new ArrayList<Map<String,Object>>();
    	List<Map<String,Object>> qcList=new ArrayList<Map<String,Object>>();
    	List<Map<String,Object>> list=qmTestRecordService.getTestRecordList(params);
  		for(Map<String,Object> map : list) {
			String result_type=map.get("result_type")!=null ? map.get("result_type").toString() : "";
  			if(result_type.equals("0")) {
  				productList.add(map);
  			}
  			if(result_type.equals("1")) {
  				qcList.add(map);
  			}
  		}
    	return R.ok().put("product_data", productList).put("qc_data", qcList);
  	}
	

    // 查询抬头数据
    @RequestMapping("/getHeadList")
 	public List<Map<String,Object>> getHeadList(@RequestParam Map<String,Object> params){
    	
    	List<Map<String,Object>> headList=qmTestRecordService.getHeadList(params);
 		return headList;
 	}
    
}
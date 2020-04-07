package com.byd.web.wms.qc.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.common.utils.TagUtils;
import com.byd.web.wms.config.entity.SysDictEntity;
import com.byd.web.wms.qc.entity.WmsQcInspectionItemEntity;
import com.byd.web.wms.qc.entity.WmsQcResultEntity;
import com.byd.web.wms.qc.service.WmsQcServiceRemote;


/**
 * 送检单抬头
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@RestController
@RequestMapping("qc/wmsqcinspectionhead")
public class WmsQcInspectionHeadController {
	@Autowired
	UserUtils userUtils;
	
	@Autowired
	TagUtils tagUtils;
	/**
	 * wms-business 服务
	 */
	@Autowired
	WmsQcServiceRemote qcServiceRemote;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return qcServiceRemote.list(params);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return qcServiceRemote.info(id);
    }

    /**
     * 未质检 - 批量质检 & 单批质检  保存
     * @param batchInscpectionResults
     * @return
     */
    @RequestMapping("/saveinspectionbatch")
    public R saveNoInspectionBatch(@RequestBody List<WmsQcInspectionItemEntity> params){
    	Map<String,Object> paramsMap = new HashMap<String, Object>();
		Map<String,Object> currentUser = userUtils.getUser();
    	paramsMap.put("params", params);
    	paramsMap.put("staffNumber", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    	//设置质检结果文本，目前前端只传了code
    	params.forEach(result -> {
			for(SysDictEntity dict : tagUtils.wmsDictList("QC_RESULT_CODE")) {
				if(dict.getCode().equals(result.getQcResultCode())) {
					result.setQcResultText(dict.getValue());
					break;
				}
			}
		});
    	return qcServiceRemote.saveNoInspectionBatch(paramsMap);
    }

    
    /**
     * 质检中-批量保存
     * @param results
     * @return
     */
    @RequestMapping("/saveOnInspect")
    public R saveOnInspect(@RequestBody List<WmsQcResultEntity> results){
    
    		Map<String,Object> paramsMap = new HashMap<>();
    		paramsMap.put("staffNumber", userUtils.getTokenUsername());
    		paramsMap.put("params", results);
    		return  qcServiceRemote.saveOnInspect(paramsMap);
		
    }
    /**
     * 质检中-单批质检保存
     * @param results
     * @return
     */
    @RequestMapping("/saveOnInspectSingleBatch")
    public R saveOnInspectionSingleBatch(@RequestBody List<WmsQcResultEntity> results){
    		
    		Map<String,Object> paramsMap = new HashMap<>();
    		paramsMap.put("staffNumber", userUtils.getTokenUsername());
    		paramsMap.put("params", results);
    		return  qcServiceRemote.saveOnInspectionSingleBatch(paramsMap);
    
    }
    
    @RequestMapping("/hasInspectedRejudgeSave")
    public R hasInspectedRejudgeSave(@RequestBody List<WmsQcResultEntity> results){
    		
    		Map<String,Object> paramsMap = new HashMap<>();
    		paramsMap.put("staffNumber", userUtils.getTokenUsername());
    		paramsMap.put("params", results);
    		return qcServiceRemote.hasInspectedRejudgeSave(paramsMap);
    	
    }

    /**
     * 创建库存复检单
     * @param stock
     * @return
     */
    @RequestMapping("/add_stock_rejudge_inspect")
    public R addStockRejudgeInspect(@RequestBody List<Map<String,Object>> stocks){
		Map<String,Object> paramsMap = new HashMap<>();
		paramsMap.put("staffNumber",userUtils.getTokenUsername());
		paramsMap.put("params", stocks);
		R r =  qcServiceRemote.addStockRejudgeInspect(paramsMap);
		String inspectNo  =   (String) r.get("inspectNo");
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
    	return R.ok().put("inspectNo", inspectNo).put("CREATOR",userUtils.getTokenUsername()).put("CREATE_DATE", curDate);
    }
    
    /**
     * 库存复检 未质检 保存
     * @param items
     * @return
     */
    @RequestMapping("/saveStockRejudgeNotInspect")
    public R saveStockRejudgeNotInspect(@RequestBody List<Map<String,Object>> items){
		//设置用户信息，从redis获取当前登录的用户
    	String FULL_NAME = (String) userUtils.getUser().get("FULL_NAME");
		items.forEach( i -> i.put("USERNAME", FULL_NAME));
    	return qcServiceRemote.saveStockRejudgeNotInspect(items);
    }
    
    /**
     * 库存复检 质检中 保存
     * @param items
     * @return
     */
    @RequestMapping("/saveStockRejudgeOnInspect")
    public R saveStockRejudgeOnInspect(@RequestBody List<Map<String,Object>> items){
    	//设置用户信息，从redis获取当前登录的用户
    	String FULL_NAME = (String) userUtils.getUser().get("FULL_NAME");
		items.forEach( i -> i.put("USERNAME", FULL_NAME));
    	return qcServiceRemote.saveStockRejudgeOnInspect(items);
    }
}

package com.byd.wms.business.modules.qc.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.StringUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionHeadEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionHeadService;
import com.byd.wms.business.modules.qc.service.WmsQcStockRejudge;





/**
 * 送检单抬头
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@RestController
@RequestMapping("qc/wmsqcinspectionhead")
public class InspectionHeadController {
    @Autowired
    private WmsQcInspectionHeadService wmsQcInspectionHeadService;
    @Autowired
    private WmsQcStockRejudge stockRejudgeService;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	params.put("FULL_NAME", user.get("FULL_NAME"));
    	params.put("USERNAME", user.get("USERNAME"));
        PageUtils page = wmsQcInspectionHeadService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsQcInspectionHeadEntity wmsQcInspectionHead = wmsQcInspectionHeadService.selectById(id);

        return R.ok().put("wmsQcInspectionHead", wmsQcInspectionHead);
    }

    
    /**
     * 未质检 - 批量质检 & 单批质检  保存
     * @param batchInscpectionResults
     * @return
     */
    @RequestMapping("/saveinspectionbatch")
    public R saveNoInspectionBatch(@RequestBody Map<String,Object> paramsMap){
    	try {
    		//JOSN2Object
    		List<WmsQcInspectionItemEntity> batchInscpectionResults =  JSON.parseArray(JSON.toJSONString(paramsMap.get("params")), WmsQcInspectionItemEntity.class);
    		String staffNumber = (String) paramsMap.get("staffNumber");
    		batchInscpectionResults.forEach(m->{
    			if(StringUtils.isBlank(m.getQcPeople()))
    				m.setQcPeople(staffNumber);
    		});
    		wmsQcInspectionHeadService.saveBatchInspectionResult(batchInscpectionResults,staffNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
    	return R.ok();
    }
    
    /**
     * 质检中-批量保存
     * @param results
     * @return
     */
    @RequestMapping("/saveOnInspect")
    public R saveOnInspect(@RequestBody Map<String, Object> paramsMap){
    	try {
    		//map -> json string -> object
    		List<WmsQcResultEntity> results = JSON.parseArray(JSON.toJSONString(paramsMap.get("params")), WmsQcResultEntity.class);
    		String staffNumber = (String) paramsMap.get("staffNumber");
    		wmsQcInspectionHeadService.saveOnInspection(results,staffNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error();
		}
    	return R.ok();
    }
    /**
     * 质检中-单批质检保存
     * @param results
     * @return
     */
    @RequestMapping("/saveOnInspectSingleBatch")
    public R saveOnInspectionSingleBatch(@RequestBody Map<String, Object> paramsMap){
    	try {
    		List<WmsQcResultEntity> results = JSON.parseArray(JSON.toJSONString(paramsMap.get("params")), WmsQcResultEntity.class);
    		String staffNumber = (String) paramsMap.get("staffNumber");
    		wmsQcInspectionHeadService.saveOnBatchInspection(results,staffNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error();
		}
    	return R.ok();
    }
    
    @RequestMapping("/hasInspectedRejudgeSave")
    public R hasInspectedRejudgeSave(@RequestBody Map<String, Object> paramsMap){
    	
    	try {
    		List<WmsQcResultEntity> results = JSON.parseArray(JSON.toJSONString(paramsMap.get("params")), WmsQcResultEntity.class);
    		String staffNumber = (String) paramsMap.get("staffNumber");
    		wmsQcInspectionHeadService.reJudgeSave(results,staffNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error();
		}
    	return R.ok();
    }

    /**
     * 创建库存复检单 
     * @param stock
     * @return
     */
    @RequestMapping("/add_stock_rejudge_inspect")
    public R addStockRejudgeInspect(@RequestBody Map<String, Object> paramsMap){
    	String staffNumber = (String) paramsMap.get("staffNumber");
    	@SuppressWarnings("unchecked")
		List<Map<String,Object>> stocks = (List<Map<String, Object>>) paramsMap.get("params");
    	String inspectNo  =  wmsQcInspectionHeadService.addStockRejudgeInspect(stocks,staffNumber);
    	//SysUserEntity currentUser = ShiroUtils.getUserEntity();
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
    	return R.ok().put("inspectNo", inspectNo).put("CREATOR",staffNumber)
    			.put("CREATE_DATE", curDate);
    }
    
    /**
     * 库存复检 未质检 保存
     * @param items
     * @return
     */
    @RequestMapping("/saveStockRejudgeNotInspect")
    public R saveStockRejudgeNotInspect(@RequestBody List<Map<String,Object>> items){
    	try {
			stockRejudgeService.saveStockRejudgeNotInspect(items);
		} catch (IllegalAccessException e) {
			return R.error();
		}
    	return R.ok();
    }
    
    /**
     * 库存复检 质检中 保存
     * @param items
     * @return
     */
    @RequestMapping("/saveStockRejudgeOnInspect")
    public R saveStockRejudgeOnInspect(@RequestBody List<Map<String,Object>> items){
    	stockRejudgeService.saveStockRejudgeOnInspect(items);
    	return R.ok();
    }
}

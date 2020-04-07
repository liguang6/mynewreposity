package com.byd.zzjmes.modules.report.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.zzjmes.modules.report.service.PmdReportService;

/**
 * 自制件报表通用控制器
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("report")
public class PmdReportController {
    @Autowired
    private PmdReportService pmdReportService; 
    
    /**
     * 订单产量达成明细报表
     * @param params
     * @return
     */
    @RequestMapping("/pmdOutputReachReport")
    public R pmdOutputReachReport(@RequestParam Map<String, Object> params){
    	PageUtils page = pmdReportService.pmdOutputReachReport(params);
        return R.ok().put("page", page);
    }
    
	/**
	 *  订单产量达成明细报表导出
	 */
	@RequestMapping("/getPmdOutputReachList")
    public R getPmdOutputReachList(@RequestParam Map<String, Object> params) throws Exception{
		List<Map<String,Object>> entityList =(List<Map<String, Object>>) pmdReportService.getPmdOutputReachList(params);
		return R.ok().put("data", entityList);
	}
	
    /**
     * 批次产量达成明细报表
     * @param params
     * @return
     */
    @RequestMapping("/batchOutputReachReport")
    public R batchOutputReachReport(@RequestParam Map<String, Object> params){
    	PageUtils page = pmdReportService.batchOutputReachReport(params);
        return R.ok().put("page", page);
    }
    
	/**
	 *  批次产量达成明细报表导出
	 */
	@RequestMapping("/getBatchOutputReachList")
    public R getBatchOutputReachList(@RequestParam Map<String, Object> params) throws Exception{
		List<Map<String,Object>> entityList =(List<Map<String, Object>>) pmdReportService.getBatchOutputReachList(params);
		return R.ok().put("data", entityList);
	}
	
    /**
     * 订单需求达成报表
     * @param params
     * @return
     */
    @RequestMapping("/pmdReqReachReport")
    public R pmdReqReachReport(@RequestParam Map<String, Object> params){
    	PageUtils page = pmdReportService.pmdReqReachReport(params);
        return R.ok().put("page", page);
    }
    
    /**
     * 订单批次需求达成报表
     * @param params
     * @return
     */
    @RequestMapping("/orderBatchReachReport")
    public R orderBatchReachReport(@RequestParam Map<String, Object> params){
    	PageUtils page = pmdReportService.orderBatchReachReport(params);
        return R.ok().put("page", page);
    }
    
    /**
     * 班组计划达成报表
     * @param params
     * @return
     */
    @RequestMapping("/workgroupReachReport")
    public R workgroupReachReport(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list = pmdReportService.getWorkgroupReachList(params);
        return R.ok().put("data", list);
    }
    
    /**
     * 组合件加工进度报表
     * @param params
     * @return
     */
    @RequestMapping("/orderAssemblyReport")
    public R orderAssemblyReport(@RequestParam Map<String, Object> params){
    	PageUtils page = pmdReportService.orderAssemblyReport(params);
    	return R.ok().put("page", page);
    }
    
	/**
	 *  组合件加工进度报表导出
	 */
	@RequestMapping("/getOrderAssemblyList")
    public R getOrderAssemblyList(@RequestParam Map<String, Object> params) throws Exception{
		List<Map<String,Object>> entityList =(List<Map<String, Object>>) pmdReportService.getOrderAssemblyList(params);
		return R.ok().put("data", entityList);
    }

    /**
     * 工序工时-加工时长报表
     * @param params
     * @return
     */
    @RequestMapping("/pmdProcessTimeReport")
    public R pmdProcessTimeReport(@RequestParam Map<String, Object> params){
        PageUtils page = pmdReportService.pmdProcessTimeReport(params);
        return R.ok().put("page", page);
    }

    /**
     * 工序工时-流转时间报表
     * @param params
     * @return
     */
    @RequestMapping("/pmdProcessTransferTimeReport")
    public R pmdProcessTransferTimeReport(@RequestParam Map<String, Object> params){
        PageUtils page = pmdReportService.pmdProcessTransferTimeReport(params);
        return R.ok().put("page", page);
    }

}
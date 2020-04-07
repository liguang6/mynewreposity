package com.byd.web.qms.processQuality.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.qms.processQuality.service.QmsProcessTestRemote;

@RestController
@RequestMapping("processQuality/test")
public class QmsProcessTestController {
	@Autowired
	 private QmsProcessTestRemote qmsProcessTestRemote;

	@RequestMapping("/getProcessTestList")
	 public R getProcessTestList(@RequestParam Map<String, Object> params){
	    return qmsProcessTestRemote.getProcessTestList(params);
	 }

	@RequestMapping("/getFaultList")
	 public R getFaultList(@RequestParam Map<String, Object> params){
	    return qmsProcessTestRemote.getFaultList(params);
	 }
	
	@RequestMapping("/saveTestRecord")
	 public R saveTestRecord(@RequestParam Map<String, Object> params){
	    return qmsProcessTestRemote.saveTestRecord(params);
	 }
	
	@RequestMapping("/saveAbnormalInfo")
	 public R saveAbnormalInfo(@RequestParam Map<String, Object> params){
	    return qmsProcessTestRemote.saveAbnormalInfo(params);
	 }
	
	@RequestMapping("/delAbnormalInfo")
	 public R delAbnormalInfo(@RequestParam(value="ABNORMAL_ID") String ABNORMAL_ID,@RequestParam(value="RECORD_ID") String RECORD_ID){
	    return qmsProcessTestRemote.delAbnormalInfo(ABNORMAL_ID,RECORD_ID);
	 }
	/**
	 * 整车品质记录列表查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/getProcessTestRecordList")
	 public R getProcessTestRecordList(@RequestParam Map<String, Object> params){
	    return qmsProcessTestRemote.getProcessTestRecordList(params);
	 }
	
	/**
	 * 整车品质记录明细查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/getProcessTestDetail")
	public R getProcessTestDetail(@RequestParam Map<String, Object> params) {
		return qmsProcessTestRemote.getProcessTestDetail(params);
	}
	
	/**
	 * 零部件品质记录列表查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/getPartsTestRecordList")
	public R getPartsTestRecordList(@RequestParam Map<String, Object> params) {
		return qmsProcessTestRemote.getPartsTestRecordList(params);
	}
	
    /**
     * DPU报表数据查询
     * @return
     */
    @RequestMapping("/getProcessDpuData")
    public R getProcessDpuData(@RequestParam Map<String,Object> condMap) {
    	return qmsProcessTestRemote.getProcessDpuData(condMap);
    }
    
    /**
     * QE确认
     * @param condMap
     * @return
     */
    @RequestMapping("/confirmProcessTest")
    public R confirmProcessTest(@RequestParam Map<String,Object> condMap) {
		return qmsProcessTestRemote.confirmProcessTest(condMap);
    	
    }
    
    /**
     * 一次交检合格率报表数据查询
     * @return
     */
    @RequestMapping("/getProcessFTYData")
    public R getProcessFTYData(@RequestParam Map<String,Object> condMap) {
    	return qmsProcessTestRemote.getProcessFTYData(condMap);
    }
    
    /**
     * 非制程品质检验记录查询
     * @param condMap
     * @return
     */
    @RequestMapping("/getUnProcessTestList")
    public R getUnProcessTestList(@RequestParam Map<String,Object> condMap) {
    	return qmsProcessTestRemote.getUnProcessTestList(condMap);
    }
    
    /**
     * 保存非制程品质检验记录
     * @param condMap
     * @return
     */
    @RequestMapping("/saveUnTestRecord")
    public R saveUnTestRecord(@RequestParam Map<String,Object> condMap) {
    	return qmsProcessTestRemote.saveUnTestRecord(condMap);
    }
    
    /**
     * 非制程品质汇总报表数据查询
     * @return
     */
    @RequestMapping("/getUnProcessReportData")
    public R getUnProcessReportData(@RequestParam Map<String,Object> condMap) {
    	return qmsProcessTestRemote.getUnProcessReportData(condMap);
    }
    
    /**
     * 部件一次交检合格率报表数据查询
     * @return
     */
    @RequestMapping("/getBJFTYData")
    public R getBJFTYData(@RequestParam Map<String,Object> condMap) {
    	return qmsProcessTestRemote.getBJFTYData(condMap);
    }
    
    /**
     * 报表数据查询
     * @return
     */
    @RequestMapping("/getFaultScatterData")
    public R getFaultScatterData(@RequestParam Map<String,Object> condMap) {
    	return qmsProcessTestRemote.getFaultScatterData(condMap);
    }
    
    
}

package com.byd.web.qms.processQuality.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.qms.processQuality.service.QmsPatrolRecordRemote;
/**
 * 品质巡检记录
 * @author cscc tangj
 * @email 
 * @date 2019-07-26 09:12:08
 */
@RestController
@RequestMapping("qms/patrolRecord")
public class QmsPatrolRecordController {
    @Autowired
    private QmsPatrolRecordRemote qmsPatrolRecordRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return qmsPatrolRecordRemote.queryPage(params);
    }
    /**
     * 模板明细列表
     */
    @RequestMapping("/getTemplateList")
    public R getTemplateList(@RequestParam Map<String, Object> params){
    	return qmsPatrolRecordRemote.getTemplateList(params);
    }
	
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	try {
    		Map<String,Object> user = userUtils.getUser();
    		params.put("user", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			R r=qmsPatrolRecordRemote.save(params);
	    	return r;
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 保存or更新
     */
    @RequestMapping("/saveOrUpdate")
    public R saveOrUpdate(@RequestParam Map<String, Object> params){
    	try {
    		Map<String,Object> user = userUtils.getUser();
    		params.put("user", user.get("USERNAME")+"："+user.get("FULL_NAME"));
			R r=qmsPatrolRecordRemote.saveOrUpdate(params);
	    	return r;
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 
     */
    @RequestMapping("/delete/{patrolRecordNo}")
    public R delete(@PathVariable("patrolRecordNo") String patrolRecordNo){
    	return qmsPatrolRecordRemote.delete(patrolRecordNo);
    }
    /**
     * 明细列表
     */
    @RequestMapping("/queryDetail")
    public R queryDetail(@RequestParam Map<String, Object> params){
    	return qmsPatrolRecordRemote.queryDetail(params);
    }
    // 处理空值
    public boolean isNotNull(Object str) {
    	boolean result=false;
    	if(str==null) {
    		return result;
    	}
    	if(str.toString().trim().equals("")) {
    		return result;
    	}
    	return true;
    }
}
package com.byd.qms.modules.processQuality.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.qms.modules.processQuality.service.QmsPatrolRecordService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 品质巡检记录
 * @author cscc tangj
 * @email 
 * @date 2019-07-26 10:12:08
 */
@RestController
@RequestMapping("processQuality/patrolRecord")
public class QmsPatrolRecordController {
    @Autowired
    private QmsPatrolRecordService qmsPatrolRecordService; 
    
    /**
     * 抬头列表
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestBody Map<String, Object> params){
        PageUtils page = qmsPatrolRecordService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 行项目列表
     */
    @RequestMapping("/queryDetail")
    public R queryDetail(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list = qmsPatrolRecordService.getList(params);
        PageUtils page = new PageUtils(list, list.size(), list.size(), 1);
        return R.ok().put("page", page);
    }
    /**
     * 获取模板数据
     */
    @RequestMapping("/getTemplateList")
    public R getTemplateList(@RequestBody Map<String, Object> params){
    	try {
    		List<Map<String,Object>> list=qmsPatrolRecordService.getTemplateList(params);
	    	return R.ok().put("data", list);
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
	
    /**
     * 保存导入数据
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
    	try {
        	params.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        	params.put("testor",params.get("user"));
        	params.put("testDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        	Gson gson=new Gson();
        	List<Map<String, Object>> list =
        			gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String, Object>>>() {}.getType());
    		int patrolRecordItemNo=1;
        	for(Map<String, Object> map : list) {
        		map.put("patrolRecordItemNo", patrolRecordItemNo+"");
        		patrolRecordItemNo++;
    		}
    		params.put("list", list);
        	String patrolRecordNo=qmsPatrolRecordService.save(params);
    		// 返回模板编号
	    	return R.ok().put("patrolRecordNo", patrolRecordNo);
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 保存编辑数据（新增or修改）
     */
    @RequestMapping("/saveOrUpdate")
    public R saveOrUpdate(@RequestBody Map<String, Object> params){
    	try {
    		params.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		Gson gson=new Gson();
    		List<Map<String, Object>> list =
        			gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String, Object>>>() {}.getType());
    		params.put("updateList", list);
    		int updateCount=qmsPatrolRecordService.update(params);
	    	return R.ok();
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 删除
     */
    @RequestMapping("/delete/{patrolRecordNo}")
    public R delete(@PathVariable("patrolRecordNo") String patrolRecordNo){
    	
    	int result=qmsPatrolRecordService.delete(patrolRecordNo);
		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
    }
    
    // 处理空值
    public boolean isNull(Object str) {
    	boolean result=true;
    	if(str==null) {
    		return result;
    	}
    	if(str.toString().trim().equals("")) {
    		return result;
    	}
    	return false;
    }
 
}
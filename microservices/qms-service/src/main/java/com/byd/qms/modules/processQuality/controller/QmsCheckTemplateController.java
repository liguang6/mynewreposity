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
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.qms.modules.processQuality.service.QmsCheckTemplateService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 品质检验记录模板
 * @author cscc tangj
 * @email 
 * @date 2019-07-24 14:12:08
 */
@RestController
@RequestMapping("processQuality/checkTemplate")
public class QmsCheckTemplateController {
    @Autowired
    private QmsCheckTemplateService qmsCheckTemplateService; 
    
    /**
     * 抬头列表
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestBody Map<String, Object> params){
        PageUtils page = qmsCheckTemplateService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 行项目列表
     */
    @RequestMapping("/queryDetail")
    public R queryDetail(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list = qmsCheckTemplateService.getItemList(params);
        PageUtils page = new PageUtils(list, list.size(), list.size(), 1);
        return R.ok().put("page", page);
    }
    
	// 模板导入预览
	@RequestMapping("/previewExcel")
	public R previewExcel(@RequestBody Map<String,Object> params){
		List<Map<String,Object>> entityList = (List<Map<String,Object>>)params.get("entityList");
		List<Map<String,Object>> saveList=new ArrayList<Map<String,Object>>();
		String msg="";
		int rowNo=0;
		for(Map<String,Object> map:entityList){
			map.put("tempItemNo", ++rowNo);
			if(isNull(map.get("testItem"))) {	
				msg+="检验项目不能为空;";
			}
			if(isNull(map.get("testItem"))) {	
				msg+="检验标准不能为空;";
			}
			map.put("msg", msg);
			saveList.add(map);
			msg="";
		}
		return R.ok().put("data", saveList);
	}
	
    /**
     * 保存导入数据
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
    	try {
        	params.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        	Gson gson=new Gson();
        	List<Map<String, Object>> list =
        			gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String, Object>>>() {}.getType());
    		for(Map<String, Object> map : list) {
    			// 将“是”、“否”转换成“X”、“0”
    			map.put("numberFlag", replaceFlag(map.get("numberFlag")));
    			map.put("onePassedFlag", replaceFlag(map.get("onePassedFlag")));
    			map.put("photoFlag", replaceFlag(map.get("photoFlag")));
    			map.put("patrolFlag", replaceFlag(map.get("patrolFlag")));
    		}
    		params.put("list", list);
        	String tempNo=qmsCheckTemplateService.save(params);
    		// 返回模板编号
	    	return R.ok().put("tempNo", tempNo);
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
        	String tempNo=qmsCheckTemplateService.saveOrUpdate(params);
    		// 返回模板编号
	    	return R.ok().put("tempNo", tempNo);
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 删除
     */
    @RequestMapping("/delete/{tempNo}")
    public R delete(@PathVariable("tempNo") String tempNo){
    	
    	int result=qmsCheckTemplateService.delete(tempNo);
		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
    }
    /**
     * 删除
     */
    @RequestMapping("/deleteItem/{id}")
    public R deleteItem(@PathVariable("id") Long id){
    	
    	int result=qmsCheckTemplateService.deleteItemById(id);
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
 
    public String replaceFlag(Object str) {
    	String result="0";
    	if(str==null) {
    		return "0";
    	}
    	if(str.toString().trim().equals("")) {
    		return "0";
    	}
    	if(str.toString().trim().equals("是")) {
    		return "X";
    	}
    	if(str.toString().trim().equals("否")) {
    		return "0";
    	}
    	return result;
    }
}
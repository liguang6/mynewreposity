package com.byd.web.wms.common.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.common.service.WmsCBatchPlanRulesRemote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/** 
 * @author 作者 E-mail: peng.tao1@byd
 * @version 创建时间：2018年8月28日 下午2:45:43 
 * 类说明 
 */
@RestController
@RequestMapping("/common/batchRules")
public class WmsCBatchPlanRulesController {
	@Autowired
	WmsCBatchPlanRulesRemote wmsCBatchPlanRulesRemote;
	@Autowired
	private UserUtils userUtils;
	
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return wmsCBatchPlanRulesRemote.list(params);
	}
	
	@RequestMapping("/querybatchcode")
    public R queryBatchCode(@RequestParam Map<String, Object> params){
		return wmsCBatchPlanRulesRemote.queryBatchCode(params);
	}
	
	@RequestMapping("/listBatchOnly")
	public R queryBatchOnly(@RequestParam Map<String, Object> params){
		return wmsCBatchPlanRulesRemote.queryBatchOnly(params);
	}
	
	@RequestMapping("/save")
	public R add(@RequestParam Map<String, Object> params){
		params.put("USERNAME", userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		return wmsCBatchPlanRulesRemote.add(params);
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCBatchPlanRulesRemote.info(id);
    }
    
    @RequestMapping("/update")
	public R update(@RequestParam Map<String, Object> params){
    	params.put("USERNAME", userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
    	return wmsCBatchPlanRulesRemote.update(params);
	}
    
    @RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		return wmsCBatchPlanRulesRemote.deletes(ids);
	}
    
    /**
     * 复制保存
     * @param entitys
     * @return
     */
    @RequestMapping("/copy")
    public R copy(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	Gson gson=new Gson();
    	List<Map<String,Object>> list =gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		list.forEach(k->{
			k=(Map<String,Object>)k;	
			k.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
	    	k.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		});
		return wmsCBatchPlanRulesRemote.saveCopyData(list);
    }
}

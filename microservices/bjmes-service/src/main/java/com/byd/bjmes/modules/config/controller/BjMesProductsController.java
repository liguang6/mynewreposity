package com.byd.bjmes.modules.config.controller;

import java.util.ArrayList;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.byd.bjmes.modules.config.service.BjMesProductsService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月15日 下午4:32:21 
 * 类说明 
 */
@RestController
@RequestMapping("config/bjMesProducts")
public class BjMesProductsController {
	
	@Autowired
    private BjMesProductsService bjMesProductsService;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
        PageUtils page = bjMesProductsService.queryPage(params);

        return R.ok().put("page", page);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> param){
    	Map<String,Object> currentUser = userUtils.getUser();
    	param.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
    	param.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	
    	Map<String,Object> map=new HashMap<String,Object>();
    	if(param.get("product_code")!=null){
	    	map.put("product_code", param.get("product_code"));
	    	List<Map<String,Object>> list=bjMesProductsService.getList(map);
	    	if(list.size()>0) {
	    		return R.error("数据已维护!");
	    	}
    	}
    	int result=bjMesProductsService.save(param);

    	return result>0 ? R.ok(): R.error("新增失败，请联系管理员");
    }
    
    /**
     * 保存组件
     */
    @RequestMapping("/saveComp")
    public R saveComp(@RequestParam Map<String, Object> param){
    	Map<String,Object> currentUser = userUtils.getUser();
    	
    	String jarrStr= param.get("ARRLIST").toString();
    	JSONArray jarr = JSON.parseArray(jarrStr);
    	List<Map<String,Object>> productlist= new ArrayList<Map<String,Object>>();
    	for(int i=0;i<jarr.size();i++){
    		
    		Map<String,Object> mapcond=new HashMap<String,Object>();
    		mapcond.put("parent_process_code", jarr.getJSONObject(i).getString("parent_process_code"));
    		mapcond.put("parent_process_name", jarr.getJSONObject(i).getString("parent_process_name"));
    		mapcond.put("parent_product_id", jarr.getJSONObject(i).getString("parent_product_id"));
    		
    		mapcond.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
    		mapcond.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		productlist.add(mapcond);
    	}
    	
    	int result=bjMesProductsService.savebatch(productlist);

    	return result>0 ? R.ok(): R.error("新增失败，请联系管理员");
    }
    
    /**
     * 明细信息
     */
    @RequestMapping("/info/{id}")
    public R queryDetail(@PathVariable("id") Long id){
        Map<String,Object> entity = bjMesProductsService.getById(id);
        return R.ok().put("product", entity);
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> param){
    	Map<String,Object> currentUser = userUtils.getUser();
    	param.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
    	param.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	
    	int result=bjMesProductsService.update(param);
        
    	return result>0 ? R.ok(): R.error("更新失败，请联系管理员");
    }

    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	
    	int result=bjMesProductsService.delete(id);
		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
    }
    
    /**
     * 删除
     */
    @RequestMapping("/deleteByParentId/{id}")
    public R deleteByParentId(@PathVariable("id") Long id){
    	
    	int result=bjMesProductsService.deleteByParentId(id);
		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
    }
    
    /**
     * 明细信息 通过parent_id
     */
    @RequestMapping("/infoByParentId/{id}")
    public R queryDetailByParentId(@PathVariable("id") Long id){
    	List<Map<String,Object>> retlist = bjMesProductsService.getByParentId(id);
        return R.ok().put("data", retlist);
    }

    /**
     * 列表(不分页)
     */
    @RequestMapping("/getList")
    public R getList(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list = bjMesProductsService.getList(params);

        return R.ok().put("data", list);
    }
    /**
     * 更新检测节点
     */
    @RequestMapping("/updateTestNode")
    public R updateTestNode(@RequestParam Map<String, Object> param){
    	String jarrStr= param.get("savedata").toString();
    	JSONArray jarr = JSON.parseArray(jarrStr);
    	List<Map<String,Object>> productlist= new ArrayList<Map<String,Object>>();
    	for(int i=0;i<jarr.size();i++){
    		Map<String,Object> mapcond=new HashMap<String,Object>();
            mapcond.put("test_node", jarr.getJSONObject(i).getString("test_node"));
            mapcond.put("id", jarr.getJSONObject(i).getString("id"));
    		productlist.add(mapcond);
        }
        param.put("detail_list", productlist);
    	int result=bjMesProductsService.updateTestNode(param);

    	return result>0 ? R.ok(): R.error("保存失败，请联系管理员");
    }

}

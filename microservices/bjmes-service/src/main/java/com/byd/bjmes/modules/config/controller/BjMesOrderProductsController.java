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
import com.byd.bjmes.modules.config.service.BjMesOrderProductsService;
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
@RequestMapping("config/bjMesOrderProducts")
public class BjMesOrderProductsController {
	
	@Autowired
    private BjMesOrderProductsService bjMesOrderProductsService;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
        PageUtils page = bjMesOrderProductsService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/getList")
    public R getList(@RequestParam Map<String, Object> params){
        List<Map<String, Object>> retlist = bjMesOrderProductsService.getList(params);

        return R.ok().put("data", retlist);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> param){
        int result=0;
    	Map<String,Object> currentUser = userUtils.getUser();
    	param.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
        param.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        
        String jarrStr= param.get("ARRLIST").toString();
    	JSONArray jarr = JSON.parseArray(jarrStr);
    	List<Map<String,Object>> orderProductlist= new ArrayList<Map<String,Object>>();
    	for(int i=0;i<jarr.size();i++){
            Map<String,Object> mapcond=new HashMap<String,Object>();
    		mapcond.put("werks", jarr.getJSONObject(i).getString("werks"));
    		mapcond.put("werks_name", jarr.getJSONObject(i).getString("werks_name"));
            mapcond.put("workshop", jarr.getJSONObject(i).getString("workshop"));
            mapcond.put("workshop_name", jarr.getJSONObject(i).getString("workshop_name"));
            mapcond.put("order_no", jarr.getJSONObject(i).getString("order_no"));
            mapcond.put("product_type_code", jarr.getJSONObject(i).getString("product_type_code_a"));
            mapcond.put("product_code", jarr.getJSONObject(i).getString("product_code_a"));
            mapcond.put("sap_no", jarr.getJSONObject(i).getString("sap_no"));
            mapcond.put("single_qty", jarr.getJSONObject(i).getString("single_qty"));
            mapcond.put("standard_hours", jarr.getJSONObject(i).getString("standard_hours"));
            mapcond.put("status", '0');
    		
    		mapcond.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
            mapcond.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
            //通过werks，order_no，product_code查询是否存在，存在则更新，不存在则新增
            Map<String,Object> updatecond=new HashMap<String,Object>();
            updatecond.put("werks", jarr.getJSONObject(i).getString("werks"));
            updatecond.put("order_no", jarr.getJSONObject(i).getString("order_no"));
            updatecond.put("product_code", jarr.getJSONObject(i).getString("product_code_a"));
            List<Map<String,Object>> retList=bjMesOrderProductsService.getList(updatecond);
            if(retList!=null&&retList.size()>0){//存在 更新
                result=bjMesOrderProductsService.update(mapcond);

            }else{//不存在 则添加
                orderProductlist.add(mapcond);
            }

        }
        
    	if(orderProductlist.size()>0){
             result=bjMesOrderProductsService.save(orderProductlist);
        }
    	

    	return  R.ok();
    }
    
    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	
    	int result=bjMesOrderProductsService.delete(id);
		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
    }

    /**
     * 列表
     */
    @RequestMapping("/queryOrderMapPage")
    public R queryOrderMapPage(@RequestParam Map<String, Object> params){
        PageUtils page = bjMesOrderProductsService.getOrderMapListByPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 保存
     */
    @RequestMapping("/saveOrderMap")
    public R saveOrderMap(@RequestParam Map<String, Object> param){
        int result=0;
        Map<String,Object> currentUser = userUtils.getUser();
    	param.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
        param.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        
        String jarrStr= param.get("ARRLIST").toString();
    	JSONArray jarr = JSON.parseArray(jarrStr);
        List<Map<String,Object>> orderMaplist= new ArrayList<Map<String,Object>>();
        for(int i=0;i<jarr.size();i++){
            Map<String,Object> mapcond=new HashMap<String,Object>();
    		mapcond.put("werks", jarr.getJSONObject(i).getString("werks"));
    		mapcond.put("werks_name", jarr.getJSONObject(i).getString("werks_name"));
            mapcond.put("workshop", jarr.getJSONObject(i).getString("workshop"));
            mapcond.put("workshop_name", jarr.getJSONObject(i).getString("workshop_name"));
            mapcond.put("order_no", jarr.getJSONObject(i).getString("order_no"));
            mapcond.put("product_type_code", jarr.getJSONObject(i).getString("product_type_code"));
            mapcond.put("map_no", jarr.getJSONObject(i).getString("map_no"));
            mapcond.put("map_name", jarr.getJSONObject(i).getString("map_name"));
            mapcond.put("memo", jarr.getJSONObject(i).getString("memo"));
    		
    		mapcond.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
            mapcond.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
            //先删除，再插入
            Map<String,Object> deletecond=new HashMap<String,Object>();
            deletecond.put("werks", jarr.getJSONObject(i).getString("werks"));
            deletecond.put("order_no", jarr.getJSONObject(i).getString("order_no"));
            deletecond.put("workshop", jarr.getJSONObject(i).getString("workshop"));
            deletecond.put("product_type_code", jarr.getJSONObject(i).getString("product_type_code"));
            result=bjMesOrderProductsService.deleteOrderMap(deletecond);
            
            orderMaplist.add(mapcond);
            
        }
        if(orderMaplist.size()>0){
            result=bjMesOrderProductsService.saveOrderMap(orderMaplist);
       }
       

       return  R.ok();
    }
    
    /**
     * 列表
     */
    @RequestMapping("/getOrderMapList")
    public R getOrderMapList(@RequestParam Map<String, Object> params){
        List<Map<String, Object>> retlist = bjMesOrderProductsService.getOrderMapList(params);

        return R.ok().put("data", retlist);
    }
    

}

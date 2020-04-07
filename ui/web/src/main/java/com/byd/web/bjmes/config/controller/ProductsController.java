package com.byd.web.bjmes.config.controller;

import java.util.Date;
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
import com.byd.web.bjmes.config.service.ProductsRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月15日 下午4:52:09 
 * 类说明 
 */
@RestController
@RequestMapping("/config/bjMesProducts")
public class ProductsController {
	@Autowired
    private ProductsRemote productsRemote;
	@Autowired
    private UserUtils userUtils;
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R queryPage(@RequestParam Map<String, Object> params){
        return productsRemote.queryPage(params);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
    	return productsRemote.save(params);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/saveComp")
    public R saveComp(@RequestParam Map<String, Object> params){
    	return productsRemote.saveComp(params);
    }
    
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return productsRemote.info(id);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
    	return productsRemote.update(params);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	return productsRemote.delById(id);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/deleteByParentId/{id}")
    public R deleteByParentId(@PathVariable("id") Long id){
    	return productsRemote.deleteByParentId(id);
    }
    
    @RequestMapping("/infoByParentId/{id}")
    public R infoByParentId(@PathVariable("id") Long id){
    	return productsRemote.infoByParentId(id);
    }

    /**
     * 根据条件查询  不分页
     */
    @RequestMapping("/getList")
    public R getList(@RequestParam Map<String, Object> params){
    	return productsRemote.getList(params);
    }

    @RequestMapping("/updateTestNode")
    public R updateTestNode(@RequestParam Map<String, Object> param){
        param.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
        param.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return productsRemote.updateTestNode(param);
    }
}

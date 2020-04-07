package com.byd.web.bjmes.config.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.bjmes.config.service.OrderProductsRemote;
import com.byd.web.bjmes.config.service.ProductsRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月15日 下午4:52:09 
 * 类说明 
 */
@RestController
@RequestMapping("/config/bjMesOrderProducts")
public class OrderProductsController {
	@Autowired
    private OrderProductsRemote orderProductsRemote;
	@Autowired
    private UserUtils userUtils;
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R queryPage(@RequestParam Map<String, Object> params){
        return orderProductsRemote.queryPage(params);
    }

    /**
     * 列表,不分页
     */
    @RequestMapping("/getList")
    public R getList(@RequestParam Map<String, Object> params){
        return orderProductsRemote.getList(params);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	return orderProductsRemote.save(params);
    }

    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	return orderProductsRemote.delById(id);
    }

    /**
     * 列表
     */
    @RequestMapping("/orderMaplist")
    public R queryOrderMapPage(@RequestParam Map<String, Object> params){
        return orderProductsRemote.queryOrderMapPage(params);
    }

    /**
     * 保存产品图号
     */
    @RequestMapping("/saveOrderMap")
    public R saveOrderMap(@RequestParam Map<String, Object> params){
    	return orderProductsRemote.saveOrderMap(params);
    }

    /**
     * 产品图号列表,不分页
     */
    @RequestMapping("/getOrderMapList")
    public R getOrderMapList(@RequestParam Map<String, Object> params){
        return orderProductsRemote.getOrderMapList(params);
    }
    
}

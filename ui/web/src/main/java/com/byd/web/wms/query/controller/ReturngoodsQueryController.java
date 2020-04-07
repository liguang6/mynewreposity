package com.byd.web.wms.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.query.service.ReturngoodsQueryRemote;

/**
 * 退货单记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-30 09:32:38
 */

@RestController
@RequestMapping("query/returngoodsQuery")
public class ReturngoodsQueryController {
    @Autowired
    private ReturngoodsQueryRemote returngoodsQueryRemote;
    /**
     * 退货查询(抬头)
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return returngoodsQueryRemote.list(params);
    }
    /**
     * 收料房退货查询
     */
    @RequestMapping("/receiveRoomOutList")
    public R receiveRoomOutList(@RequestParam Map<String, Object> params){
    	return returngoodsQueryRemote.receiveRoomOutList(params);
    }
    /**
     * 库房退货查询
     */
    @RequestMapping("/warehouseOutList")
    public R warehouseOutList(@RequestParam Map<String, Object> params){
    	return returngoodsQueryRemote.warehouseOutList(params);
    }
    /**
     * 车间退货查询
     */
    @RequestMapping("/workshopReturnList")
    public R workshopReturnList(@RequestParam Map<String, Object> params){
    	return returngoodsQueryRemote.warehouseOutList(params);
    }
    /**
     * 明细
     */
    @RequestMapping("/detail")
    public R detail(@RequestParam Map<String, Object> params){
    	return returngoodsQueryRemote.detail(params);
    }
    /**
     * 删除关闭
     */
    @RequestMapping("/del")
    public R del(@RequestParam Map<String, Object> params){
    	return returngoodsQueryRemote.del(params);
    }
    @RequestMapping("/close")
    public R close(@RequestParam Map<String, Object> params){
    	return returngoodsQueryRemote.close(params);
    }
    /**
     * 查询退货单类型
     */
    @RequestMapping("/queryReturnDocTypeList")
    public R queryReturnDocTypeList(){
    	return returngoodsQueryRemote.queryReturnDocTypeList();
    }
    /**
     * 查询退货类型
     */
    @RequestMapping("/queryReturnTypeList")
    public R queryReturnTypeList(@RequestParam String type){
    	return returngoodsQueryRemote.queryReturnTypeList(type);
    }
}
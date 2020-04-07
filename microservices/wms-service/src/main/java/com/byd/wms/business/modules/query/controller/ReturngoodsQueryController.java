package com.byd.wms.business.modules.query.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.ReturngoodsQueryService;

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
    private ReturngoodsQueryService returngoodsQueryService;
    /**
     * 退货查询(抬头)
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = returngoodsQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 收料房退货查询
     */
    @RequestMapping("/receiveRoomOutList")
    public R receiveRoomOutList(@RequestParam Map<String, Object> params){
    	PageUtils page = returngoodsQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 库房退货查询
     */
    @RequestMapping("/warehouseOutList")
    public R warehouseOutList(@RequestParam Map<String, Object> params){
    	PageUtils page = returngoodsQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 车间退货查询
     */
    @RequestMapping("/workshopReturnList")
    public R workshopReturnList(@RequestParam Map<String, Object> params){
    	PageUtils page = returngoodsQueryService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 明细
     */
    @RequestMapping("/detail")
    public R detail(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list = returngoodsQueryService.queryItemPage(params);
        return R.ok().put("list", list);
    }
    /**
     * 删除关闭
     */
    @RequestMapping("/del")
    public R del(@RequestParam Map<String, Object> params){
    	boolean isOk=returngoodsQueryService.del(params);
    	if(!isOk) {
    		return R.error("操作失败");
    	}
        return R.ok();
    }
    @RequestMapping("/close")
    public R close(@RequestParam Map<String, Object> params){
    	String msg = "";
    	try {
    		msg = returngoodsQueryService.close(params);
    	}catch (Exception e) {
			msg = e.getMessage();
			return R.error(msg);
		}
		return R.ok().put("msg",msg);
    }
    /**
     * 查询退货单类型
     */
    @RequestMapping("/queryReturnDocTypeList")
    public R queryReturnDocTypeList(){
    	List<Map<String,Object>> list = returngoodsQueryService.queryReturnDocTypeList();
        return R.ok().put("list", list);
    }
    /**
     * 查询退货类型
     */
    @RequestMapping("/queryReturnTypeList")
    public R queryReturnTypeList(@RequestParam(value="type") String type){
    	List<Map<String,Object>> list = returngoodsQueryService.queryReturnTypeList(type);
        return R.ok().put("list", list);
    }
}

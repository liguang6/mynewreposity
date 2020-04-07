package com.byd.web.wms.out.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.wms.out.service.WmsOutPickingServiceRemote;

/**
 * 需求拣配控制器
 * @author ren.wei3
 *
 */
@RestController
@RequestMapping("/out/picking")
public class WmsOutPickingController {

	@Autowired 
	private WmsOutPickingServiceRemote wmsOutPickingServiceRemote;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsOutPickingServiceRemote.list(params);
    }
    
    /**
     * 推荐
     */
    @RequestMapping("/recommend")
    public R recommend(@RequestParam Map<String, Object> params){
    	String reqno = params.get("REQUIREMENT_NO") == null?"":params.get("REQUIREMENT_NO").toString().trim();
    	params.put("REQUIREMENT_NO", reqno);
    	return wmsOutPickingServiceRemote.recommend(params);
    }
    
    /**
     * 下架
     */
    @RequestMapping("/picking")
    public R picking(@RequestParam Map<String, Object> params){
    	return wmsOutPickingServiceRemote.picking(params);
    }
    
    /**
     * 获取库存
     */
    @RequestMapping("/getStockInfo")
    public R getStockInfo(@RequestParam Map<String, Object> params){
    	return wmsOutPickingServiceRemote.getStockInfo(params);
    }
    
    /**
     * 获取可打配送标签数据（总装）
     * @param params
     * @return
     */
    @RequestMapping("/shippingLabel")
    public R queryShippingLabel(@RequestParam Map<String, Object> params){
        return wmsOutPickingServiceRemote.queryShippingLabel(params);
    }
    
    /**
     * 获取可打配送标签数据（CS）
     * @param params
     * @return
     */
    @RequestMapping("/shippingLabelcs")
    public R queryShippingLabelcs(@RequestParam Map<String, Object> params){
        return wmsOutPickingServiceRemote.queryShippingLabelcs(params);
    }
    
    /**
     * 获取关键零部件标签
     * @param params
     * @return
     */
    @RequestMapping("/keyPartsLabel")
    public R querykeyPartsLabel(@RequestParam Map<String, Object> params){
        return wmsOutPickingServiceRemote.querykeyPartsLabel(params);
    }
    
    /**
     * 保存配送标签数据
     * @param params
     * @return
     */
    @RequestMapping("/saveShippingLabel")
    public R saveShippingLabel(@RequestParam Map<String, Object> params){
        return wmsOutPickingServiceRemote.saveShippingLabel(params);
    }
    
    /**
     * 拣配清单
     */
    @RequestMapping("/pickList")
    public R pickList(@RequestParam Map<String, Object> params){
        return wmsOutPickingServiceRemote.pickList(params);
    }
    
    /**
     * 获取需求头
     */
    @RequestMapping("/getReq")
    public R getReq(@RequestParam Map<String, Object> params){
    	return wmsOutPickingServiceRemote.getReq(params);
    }
}
